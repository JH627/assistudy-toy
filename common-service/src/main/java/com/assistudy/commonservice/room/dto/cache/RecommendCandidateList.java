package com.assistudy.commonservice.room.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GenericJackson2JsonRedisSerializer는 최상위 타입이 raw List<T>일 때
 * 역직렬화 시 타입 정보를 복원하지 못해 실패한다(WRITE는 성공하지만 READ에서
 * "expected VALUE_STRING ... for subtype of java.lang.Object" 에러 발생).
 * ranking 캐시처럼 단일 객체로 감싸서 캐싱해야 정상 왕복된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendCandidateList {

    private List<RecommendCandidate> candidates;
}
