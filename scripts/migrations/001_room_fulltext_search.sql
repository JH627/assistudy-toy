-- ============================================================================
-- 001_room_fulltext_search.sql
-- 목적: GET /rooms/search 의 LIKE '%keyword%' (선행 와일드카드, 인덱스 불가) 를
--       FULLTEXT 인덱스 기반 검색으로 교체하기 위한 스키마 변경
--
-- ngram 파서를 쓰는 이유: 기본 FULLTEXT 파서는 공백으로 단어를 나누는데 한글은
--       띄어쓰기 단위가 검색 단위와 안 맞음. ngram은 N글자 단위로 겹쳐 쪼개서
--       한글/짧은 키워드 모두 잘 처리됨.
-- ngram_token_size: 기본값 2 그대로 사용 (RDS 파라미터 그룹 변경/재부팅 불필요).
--       innodb_ft_min_token_size는 ngram 파서에는 적용되지 않으므로 'k6' 같은
--       2자 키워드도 별도 설정 없이 매칭됨.
--
-- 실행: ddl-auto가 validate라 Hibernate가 만들어주지 않음 -> 반드시 수동 실행.
--       room 테이블이 작아서(수백 행) 락 걸리는 시간은 짧음.
-- ============================================================================

USE assistudy_common;

-- 실행 전 현재 파서 지원 여부/설정 확인용 (선택)
-- SHOW VARIABLES LIKE 'ngram_token_size';

ALTER TABLE room
    ADD FULLTEXT INDEX ft_room_search (name, tag_name, description) WITH PARSER ngram;

-- 검증: 인덱스가 생겼는지, 타입이 FULLTEXT인지 확인
SHOW INDEX FROM room WHERE Key_name = 'ft_room_search';

-- 검증: 실제 검색어로 EXPLAIN 찍어서 풀스캔이 아니라 fulltext 인덱스를 타는지 확인
-- (LT_room은 seed.sql로 넣은 loadtest 더미 데이터 기준 검증용 키워드)
EXPLAIN
SELECT *
FROM room
WHERE is_deleted = FALSE
  AND MATCH(name, tag_name, description) AGAINST ('"LT_room"' IN BOOLEAN MODE)
ORDER BY created_at DESC;
