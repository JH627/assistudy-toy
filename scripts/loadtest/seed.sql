-- ============================================================================
-- seed.sql
-- 목적: 쿼리 최적화 Before/After 측정을 위한 부하용 더미 데이터 시딩
-- 대상 DB: assistudy_common (common-service, RDS 단일 인스턴스 안의 논리 DB)
--
-- 안전장치:
--   - room.tag_name = 'loadtest' 로 전부 마킹
--   - room.name 은 'LT_room_%' 접두사
--   - host_user_id / participant user_id 는 900000000번대(실제 유저와 절대 겹치지 않는 범위)
--   -> cleanup.sql은 이 조건들로만 삭제하므로 실데이터는 건드리지 않음
--
-- 실행 위치: RDS가 --no-publicly-accessible이라 VPC 내부에서만 접속 가능.
--   Kafka EC2(SG_KAFKA)에서 실행. 단, SG_RDS가 기본적으로 SG_ECS의 3306만 허용하므로
--   먼저 SG_RDS에 SG_KAFKA -> 3306 인바운드 규칙을 추가해야 함(안 하면 mysql 접속이
--   거부가 아니라 무응답으로 멈춤).
--
-- 사용법:
--   1) 먼저 아래 @room_count 등을 작게 줄여서(예: 20/5/3) 드라이런으로 한 번 실행
--   2) 결과 건수를 확인한 뒤 문제 없으면 원래 숫자로 되돌려 본 실행
--   3) 트래픽이 적은 시간대 권장 (recommend/ranking API에 즉시 노출됨)
--   4) 측정(01~05번 API의 before/after)이 전부 끝날 때까지 데이터를 지우지 말 것
-- ============================================================================

USE assistudy_common;

-- ---- 조절 가능한 규모 (여기 숫자만 바꾸면 전체 볼륨이 바뀜) ----
SET @room_count       = 500;   -- 생성할 방 수 (마지막 @class_room_count 개는 CLASS 타입)
SET @class_room_count = 100;
SET @user_count        = 50;   -- 참여자로 쓸 합성 유저 수
SET @base_user_id      = 900000000;
SET @real_host_user_id = 3;    -- 실제 존재하는 유저(k6 테스트 계정). room.host_user_id 전용.
                                -- room 조회 API 대부분이 매 방마다 userServiceClient.getUserInfo(hostUserId)를
                                -- 호출하는데, 가짜 유저면 getRecommendedRooms()처럼 try/catch 없는 곳에서
                                -- 예외가 터지거나(N+1과 무관하게 측정 왜곡) 외부 호출 자체가 과도하게 누적된다.
                                -- room_participants/total_time/feedback의 user_id는 개별 getUserInfo 호출
                                -- 경로에 안 걸리므로 계속 @base_user_id 합성값을 씀.
SET @participant_rows  = 1500; -- room_participants 생성 행 수
SET @tt_rows            = 150000; -- total_time 생성 행 수 (추천/랭킹 쿼리용 핵심 볼륨)
SET @feedback_users     = 10;   -- 과제당 피드백을 남길 합성 유저 수

START TRANSACTION;

-- ----------------------------------------------------------------------------
-- 0) 순번 생성용 임시 시퀀스 테이블 (재귀 CTE 대신 곱집합으로 빠르게 생성)
-- ----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS loadtest_seq;
CREATE TEMPORARY TABLE loadtest_seq (n INT PRIMARY KEY) ENGINE=MEMORY;

INSERT INTO loadtest_seq (n)
SELECT (t1.n + t2.n*2 + t3.n*4 + t4.n*8 + t5.n*16 + t6.n*32 + t7.n*64
        + t8.n*128 + t9.n*256 + t10.n*512 + t11.n*1024 + t12.n*2048
        + t13.n*4096 + t14.n*8192 + t15.n*16384 + t16.n*32768 + t17.n*65536
        + t18.n*131072) AS n
FROM (SELECT 0 n UNION ALL SELECT 1) t1,
     (SELECT 0 n UNION ALL SELECT 1) t2,
     (SELECT 0 n UNION ALL SELECT 1) t3,
     (SELECT 0 n UNION ALL SELECT 1) t4,
     (SELECT 0 n UNION ALL SELECT 1) t5,
     (SELECT 0 n UNION ALL SELECT 1) t6,
     (SELECT 0 n UNION ALL SELECT 1) t7,
     (SELECT 0 n UNION ALL SELECT 1) t8,
     (SELECT 0 n UNION ALL SELECT 1) t9,
     (SELECT 0 n UNION ALL SELECT 1) t10,
     (SELECT 0 n UNION ALL SELECT 1) t11,
     (SELECT 0 n UNION ALL SELECT 1) t12,
     (SELECT 0 n UNION ALL SELECT 1) t13,
     (SELECT 0 n UNION ALL SELECT 1) t14,
     (SELECT 0 n UNION ALL SELECT 1) t15,
     (SELECT 0 n UNION ALL SELECT 1) t16,
     (SELECT 0 n UNION ALL SELECT 1) t17,
     (SELECT 0 n UNION ALL SELECT 1) t18
LIMIT 262144;

-- ----------------------------------------------------------------------------
-- 1) room: STUDY 타입 (room_count - class_room_count)개 + CLASS 타입 마지막 class_room_count개
-- ----------------------------------------------------------------------------
INSERT INTO room (host_user_id, name, type, tag_name, description, is_private,
                   password, mic_active, max_participants, is_deleted, created_at,
                   openvidu_session_id, is_active)
SELECT
    @real_host_user_id,
    CONCAT('LT_room_', LPAD(n, 5, '0')),
    IF(n >= @room_count - @class_room_count, 'CLASS', 'STUDY'),
    'loadtest',
    'loadtest seed data',
    FALSE,
    NULL,
    FALSE,
    20,
    FALSE,
    NOW() - INTERVAL (n % 200) DAY,
    NULL,
    TRUE
FROM loadtest_seq
WHERE n < @room_count
ORDER BY n;

-- 방금 넣은 방들에 0..room_count-1 순번을 매핑 (id는 auto_increment라 순번과 별개)
DROP TEMPORARY TABLE IF EXISTS loadtest_rooms;
CREATE TEMPORARY TABLE loadtest_rooms (idx INT PRIMARY KEY, room_id BIGINT, room_type VARCHAR(10)) ENGINE=MEMORY;

INSERT INTO loadtest_rooms (idx, room_id, room_type)
SELECT (ROW_NUMBER() OVER (ORDER BY id)) - 1 AS idx, id, type
FROM room
WHERE tag_name = 'loadtest' AND host_user_id = @real_host_user_id;

-- ----------------------------------------------------------------------------
-- 2) room_participants: 합성 유저 user_count명이 여러 방에 참여
-- ----------------------------------------------------------------------------
INSERT INTO room_participants (room_id, user_id, is_deleted)
SELECT
    lr.room_id,
    @base_user_id + (s.n % @user_count),
    FALSE
FROM loadtest_seq s
JOIN loadtest_rooms lr ON lr.idx = ((s.n * 7) + (s.n % @user_count)) % @room_count
WHERE s.n < @participant_rows;

-- ----------------------------------------------------------------------------
-- 3) total_time: 추천(01)/랭킹·잔디(04) 쿼리가 스캔할 대량 볼륨
-- ----------------------------------------------------------------------------
INSERT INTO total_time (room_id, user_id, date, total_time, focus_time, updated_at, result)
SELECT
    lr.room_id,
    @base_user_id + (FLOOR(s.n / @room_count) % @user_count),
    CURDATE() - INTERVAL ((FLOOR(s.n / (@room_count * @user_count)) * 33
                            + (FLOOR(s.n / @room_count) % @user_count)) % 200) DAY,
    600 + ((s.n * 37) % 3000),
    300 + ((s.n * 53) % 2000),
    NOW(),
    NULL
FROM loadtest_seq s
JOIN loadtest_rooms lr ON lr.idx = s.n % @room_count
WHERE s.n < @tt_rows;

-- ----------------------------------------------------------------------------
-- 4) homework: CLASS 타입 방마다 5개씩
-- ----------------------------------------------------------------------------
INSERT INTO homework (room_id, date, comment)
SELECT
    lr.room_id,
    CURDATE() - INTERVAL ((s.n % @class_room_count) + FLOOR(s.n / @class_room_count) * 20) DAY,
    CONCAT('loadtest homework #', FLOOR(s.n / @class_room_count))
FROM loadtest_seq s
JOIN loadtest_rooms lr ON lr.idx = @room_count - @class_room_count + (s.n % @class_room_count)
WHERE s.n < @class_room_count * 5
  AND lr.room_type = 'CLASS';

DROP TEMPORARY TABLE IF EXISTS loadtest_homeworks;
CREATE TEMPORARY TABLE loadtest_homeworks (idx INT PRIMARY KEY, homework_id BIGINT) ENGINE=MEMORY;

INSERT INTO loadtest_homeworks (idx, homework_id)
SELECT (ROW_NUMBER() OVER (ORDER BY h.id)) - 1 AS idx, h.id
FROM homework h
JOIN room r ON h.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.host_user_id = @real_host_user_id;

-- ----------------------------------------------------------------------------
-- 5) feedback: 과제(03의 중첩 N+1 대상)마다 합성 유저 여러 명이 피드백
-- ----------------------------------------------------------------------------
INSERT INTO feedback (date, feedback, user_id, homework_id)
SELECT
    CURDATE() - INTERVAL (s.n % 60) DAY,
    'loadtest feedback text',
    @base_user_id + (FLOOR(s.n / (@class_room_count * 5)) % @feedback_users),
    lh.homework_id
FROM loadtest_seq s
JOIN loadtest_homeworks lh ON lh.idx = s.n % (@class_room_count * 5)
WHERE s.n < (@class_room_count * 5 * @feedback_users);

COMMIT;

-- ----------------------------------------------------------------------------
-- 검증: 생성된 건수 확인
-- ----------------------------------------------------------------------------
SELECT 'room' AS tbl, COUNT(*) AS cnt FROM room WHERE tag_name='loadtest'
UNION ALL
SELECT 'room_participants', COUNT(*) FROM room_participants rp JOIN room r ON rp.room_id=r.id WHERE r.tag_name='loadtest'
UNION ALL
SELECT 'total_time', COUNT(*) FROM total_time t JOIN room r ON t.room_id=r.id WHERE r.tag_name='loadtest'
UNION ALL
SELECT 'homework', COUNT(*) FROM homework h JOIN room r ON h.room_id=r.id WHERE r.tag_name='loadtest'
UNION ALL
SELECT 'feedback', COUNT(*) FROM feedback f JOIN homework h ON f.homework_id=h.id JOIN room r ON h.room_id=r.id WHERE r.tag_name='loadtest';
