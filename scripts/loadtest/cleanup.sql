-- ============================================================================
-- cleanup.sql
-- 목적: seed.sql로 넣은 더미 데이터를 완전히 제거
--
-- 삭제 조건은 항상 두 겹으로 건다: tag_name='loadtest' AND name LIKE 'LT_room_%'
-- (fix_host_user_id.sql 실행 후 host_user_id가 실제 유저로 바뀌었기 때문에
--  더 이상 host_user_id 범위로는 안전장치를 걸 수 없음 -> name 접두사로 대체.
--  'LT_room_%'는 room.name(20자 제한, 유니크 접두사)이라 실데이터와 겹칠 일이 없음)
--
-- 실행 전 반드시 아래 SELECT로 삭제 대상 건수를 먼저 눈으로 확인할 것.
-- 01~05번 API의 before/after 측정이 전부 끝난 뒤 한 번만 실행할 것.
-- ============================================================================

USE assistudy_common;

-- ---- 0) 삭제 전 확인 (반드시 먼저 실행) ----
SELECT 'room to delete' AS what, COUNT(*) AS cnt
FROM room WHERE tag_name = 'loadtest' AND name LIKE 'LT\_room\_%'
UNION ALL
SELECT 'room_participants to delete', COUNT(*)
FROM room_participants rp JOIN room r ON rp.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%'
UNION ALL
SELECT 'total_time to delete', COUNT(*)
FROM total_time t JOIN room r ON t.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%'
UNION ALL
SELECT 'homework to delete', COUNT(*)
FROM homework h JOIN room r ON h.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%'
UNION ALL
SELECT 'feedback to delete', COUNT(*)
FROM feedback f JOIN homework h ON f.homework_id = h.id JOIN room r ON h.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%';

-- 위 결과가 예상 범위(room ~500, total_time ~150000 등)와 맞는지 확인한 뒤
-- 아래 START TRANSACTION ~ COMMIT 블록을 실행할 것.

START TRANSACTION;

DELETE f
FROM feedback f
JOIN homework h ON f.homework_id = h.id
JOIN room r ON h.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%';

DELETE h
FROM homework h
JOIN room r ON h.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%';

DELETE t
FROM total_time t
JOIN room r ON t.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%';

DELETE rp
FROM room_participants rp
JOIN room r ON rp.room_id = r.id
WHERE r.tag_name = 'loadtest' AND r.name LIKE 'LT\_room\_%';

DELETE FROM room
WHERE tag_name = 'loadtest' AND name LIKE 'LT\_room\_%';

COMMIT;

-- ---- 삭제 후 검증 (전부 0이어야 함) ----
SELECT COUNT(*) AS remaining_loadtest_rooms
FROM room WHERE tag_name = 'loadtest' AND name LIKE 'LT\_room\_%';
