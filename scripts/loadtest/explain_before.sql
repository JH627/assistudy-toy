-- ============================================================================
-- explain_before.sql
-- 목적: Top5 API의 실제 실행계획을 수정 전(before) 기준으로 캡처.
--       출력을 그대로 캡처해두면(스크린샷/텍스트) After와 나란히 비교 가능.
-- 실행: seed.sql + add_test_user_participation.sql 이후, k6 before 측정과 같은 날 실행
-- ============================================================================

USE assistudy_common;

-- ----------------------------------------------------------------------------
-- 01) GET /rooms/recommend -> TotalTimeRepository.findTopRoomsByFocusRatio()
--     기대: total_time 전체(15만 행) + room JOIN, 인덱스 없이 풀스캔/임시테이블/파일소트
-- ----------------------------------------------------------------------------
EXPLAIN
SELECT r.id
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE r.is_deleted = FALSE
GROUP BY r.id
HAVING SUM(t.total_time) > 0
ORDER BY SUM(t.focus_time) / SUM(t.total_time) DESC;

-- ----------------------------------------------------------------------------
-- 02) GET /rooms -> RoomRepository.findByIsDeletedFalse()
--     기대: is_deleted 인덱스 없어서 room 테이블 풀스캔
-- ----------------------------------------------------------------------------
EXPLAIN
SELECT * FROM room WHERE is_deleted = FALSE;

-- ----------------------------------------------------------------------------
-- 04) GET /total/ranking -> WHERE DATE(t.date) = :date (오늘)
--     기대: date 컬럼에 함수(DATE())를 씌워서 인덱스가 있어도 못 탐 -> full scan
-- ----------------------------------------------------------------------------
EXPLAIN
SELECT t.user_id, SUM(t.focus_time)
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE DATE(t.date) = CURDATE() AND r.type = 'STUDY'
GROUP BY t.user_id
ORDER BY SUM(t.focus_time) DESC
LIMIT 6;

-- ----------------------------------------------------------------------------
-- 04) GET /total/grass -> WHERE YEAR(t.date) = :year AND userId = :userId
--     기대: YEAR() 함수로 인덱스 무효화 -> 15만 행 전체 스캔
--     (아래는 add_test_user_participation.sql에서 쓴 실제 테스트 유저 id로 바꿔서 실행)
-- ----------------------------------------------------------------------------
EXPLAIN
SELECT t.date, SUM(t.focus_time)
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE t.user_id = 3 /* k6test@test.com */
  AND YEAR(t.date) = YEAR(CURDATE())
  AND r.type = 'STUDY'
GROUP BY t.date
ORDER BY t.date;

-- ----------------------------------------------------------------------------
-- 05) GET /rooms/search?keyword=LT_room -> LIKE '%keyword%' 선행 와일드카드
--     기대: name/tag_name/description 어디에도 인덱스를 못 타서 풀스캔
-- ----------------------------------------------------------------------------
EXPLAIN
SELECT * FROM room
WHERE is_deleted = FALSE
  AND (LOWER(name) LIKE LOWER('%LT_room%')
       OR LOWER(tag_name) LIKE LOWER('%LT_room%')
       OR LOWER(description) LIKE LOWER('%LT_room%'))
ORDER BY created_at DESC;

-- ----------------------------------------------------------------------------
-- 03) GET /homeworks/my-participated-rooms -> 중첩 N+1이라 EXPLAIN 한 방으론 안 보임.
--     대신 "요청 하나당 쿼리 횟수"로 증빙 (참여 CLASS방 100개 x 과제 5개 = 501회 왕복 기대)
--     -> 아래는 실제 이 API가 내부에서 반복 실행하는 두 쿼리의 실행계획만 참고용으로 확인
-- ----------------------------------------------------------------------------
EXPLAIN SELECT * FROM homework WHERE room_id = (SELECT id FROM room WHERE tag_name='loadtest' AND type='CLASS' LIMIT 1) ORDER BY date DESC;
EXPLAIN SELECT * FROM feedback WHERE homework_id = (SELECT id FROM homework WHERE room_id IN (SELECT id FROM (SELECT id FROM room WHERE tag_name='loadtest' AND type='CLASS' LIMIT 1) AS x) LIMIT 1) ORDER BY date DESC;
