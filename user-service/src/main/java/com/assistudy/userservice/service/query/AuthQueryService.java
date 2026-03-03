package com.assistudy.userservice.service.query;

public interface AuthQueryService {
    /**
     * token blacklist 확인
     * @param token 확인할 토큰
     * @return true: 블랙리스트에 포함된 토큰, false: 유효한 토큰
     */
    boolean isBlacklisted(String token);
}
