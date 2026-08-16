-- ============================================================================
-- 002_total_time_date_indexes.sql
-- 목적: GET /time/ranking, GET /time/grass 가 DATE()/YEAR() 함수를 WHERE에 써서
--       인덱스를 못 타던 문제를 해결하기 위한 인덱스 추가.
--       (앱 코드도 함께 수정: DATE()/YEAR() 제거하고 순수 비교/범위 비교로 재작성)
--
-- 인덱스를 2개로 나눈 이유:
--   - idx_total_time_user_date (user_id, date): grass 쿼리는 t.userId = : 로 걸고
--     t.date 범위로 좁히므로, 등치 컬럼(user_id)을 선행시켜야 함
--   - idx_total_time_date (date): ranking 쿼리는 userId 필터가 아예 없어서
--     (user_id, date) 인덱스로는 date만 걸 때 활용을 못 함 -> 별도 인덱스 필요
--
-- 실행: ddl-auto가 validate라 Hibernate가 안 만들어줌 -> 수동 실행 필수.
--       total_time이 15만 행이라 room/room_participants보다 인덱스 생성 시간이
--       좀 더 걸릴 수 있음(초 단위, InnoDB online DDL이라 락은 짧음).
-- ============================================================================

USE assistudy_common;

CREATE INDEX idx_total_time_user_date ON total_time (user_id, date);
CREATE INDEX idx_total_time_date ON total_time (date);

-- 검증: 인덱스 생성 확인
SHOW INDEX FROM total_time WHERE Key_name IN ('idx_total_time_user_date', 'idx_total_time_date');

-- 검증: ranking 쿼리가 idx_total_time_date를 타는지 확인
EXPLAIN
SELECT t.user_id, SUM(t.focus_time)
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE t.date = CURDATE() AND r.type = 'STUDY'
GROUP BY t.user_id
ORDER BY SUM(t.focus_time) DESC
LIMIT 6;

-- 검증: grass 쿼리가 idx_total_time_user_date를 타는지 확인 (test_user_id로 교체해서 실행)
EXPLAIN
SELECT t.date, SUM(t.focus_time)
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE t.user_id = 3 /* TODO: 실제 test_user_id로 교체 */
  AND t.date >= '2026-01-01' AND t.date < '2027-01-01'
  AND r.type = 'STUDY'
GROUP BY t.date
ORDER BY t.date;
