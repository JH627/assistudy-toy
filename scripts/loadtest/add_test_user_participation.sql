-- ============================================================================
-- add_test_user_participation.sql
-- 목적: k6 부하테스트 계정(TEST_EMAIL)을 loadtest CLASS 방 100개 전부의
--       참가자로 등록한다.
--
-- 왜 필요한가:
--   GET /homeworks/my-participated-rooms 는 로그인한 사용자 본인이 참여한 방만
--   조회한다 (WHERE rp.userId = :userId). seed.sql이 넣는 room_participants는
--   전부 합성 유저(900000001~900000050)라서, 그대로면 k6 테스트 계정 기준으로는
--   결과가 0건이라 03번(중첩 N+1) 문제가 아예 재현되지 않는다.
--   -> k6 테스트 계정을 실제로 100개 CLASS 방(각 5개 과제)의 참가자로 넣어줘야
--      이 API의 before/after 차이가 제대로 측정된다.
--
-- 사전 준비:
--   1) seed.sql 먼저 실행 완료
--   2) k6 테스트 계정(TEST_EMAIL)의 실제 user_id를 알아야 함
--      -> assistudy_user DB에서 조회 (본인 계정 정보이므로 이 방식이 안전)
--         USE assistudy_user;
--         SELECT id, email FROM users WHERE email = 'k6test@test.com';
--      -> 아래 @test_user_id 에 그 값으로 바꿔서 실행
-- ============================================================================

USE assistudy_common;

SET @test_user_id = 3; -- k6test@test.com

-- 이미 등록되어 있으면 중복 방지
DELETE rp FROM room_participants rp
JOIN room r ON rp.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.host_user_id >= 900000000
  AND rp.user_id = @test_user_id;

INSERT INTO room_participants (room_id, user_id, is_deleted)
SELECT r.id, @test_user_id, FALSE
FROM room r
WHERE r.tag_name = 'loadtest'
  AND r.host_user_id >= 900000000
  AND r.type = 'CLASS';

-- 검증: 100건이 나와야 함 (class_room_count 값과 일치)
SELECT COUNT(*) AS test_user_class_room_participations
FROM room_participants rp
JOIN room r ON rp.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.host_user_id >= 900000000
  AND rp.user_id = @test_user_id;
