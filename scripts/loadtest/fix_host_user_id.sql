-- ============================================================================
-- fix_host_user_id.sql
-- 목적: 이미 시딩된 loadtest 방들의 host_user_id(가짜 900000000대)를
--       실제 존재하는 유저로 바꾼다.
--
-- 왜 필요한가:
--   room 관련 조회 API 대부분이 방마다 userServiceClient.getUserInfo(hostUserId)를
--   호출한다. host_user_id가 실존하지 않으면:
--     - getRecommendedRooms(): try/catch 없이 바로 예외 던짐 -> 매 요청 에러
--     - getAllRooms(): try/catch는 있지만, 존재하지 않는 유저 500명을 순차 조회하며
--       외부 호출 자체가 누적되어 과도하게 느려질 수 있음
--   -> host_user_id를 실제 유저(k6 테스트 계정)로 통일해서 이 혼입 변수를 제거한다.
--      (room_participants/total_time/feedback의 user_id는 그대로 900000000대 유지 —
--       이쪽은 개별 getUserInfo 호출 경로에 안 걸림)
--
-- 실행 후: cleanup.sql이 host_user_id 조건 대신 name 패턴으로 안전장치를 걸도록
--          이미 같이 수정해뒀으니 별도 조치 불필요.
-- ============================================================================

USE assistudy_common;

SET @test_user_id = 3; -- 실제 k6 테스트 계정(k6test@test.com)의 user_id

UPDATE room
SET host_user_id = @test_user_id
WHERE tag_name = 'loadtest' AND name LIKE 'LT\_room\_%';

SELECT COUNT(*) AS updated_rooms
FROM room
WHERE tag_name = 'loadtest' AND host_user_id = @test_user_id;
