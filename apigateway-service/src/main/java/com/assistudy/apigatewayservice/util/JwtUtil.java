package com.assistudy.apigatewayservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.public-key}")
    private String publicKeyBase64;
    private PublicKey publicKey;

    private byte[] parseKeyBytes(String keyBase64) {
        return Base64.getDecoder().decode(keyBase64.replaceAll("\\s", ""));
    }

    @PostConstruct
    public void init() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(parseKeyBytes(publicKeyBase64));
        publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Token Claims 추출.
     *
     * @param token Json Web Token
     * @return All Claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isVaild(String accessToken) {
        try {
            // 서명 및 만료 검증 (예외 발생 시 catch)
            validateToken(accessToken);
            // 만료 여부 추가 확인 (예외가 안 났어도 만료일이 지났을 수 있음)
            if (isTokenExpired(accessToken)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // 변조, 만료, 파싱 오류 등
            return false;
        }
    }

    /**
     * 사용자 ID 추출
     *
     * @param token Json Web Token
     * @return 사용자 ID
     */
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    /**
     * 만료 일자 추출
     *
     * @param token Json Web Token
     * @return 만료 일자
     */
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * 만료 여부
     *
     * @param token JWT token
     * @return 만료 여부
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 토큰 검증 (서명 및 만료시간만 검증)
     *
     * @param token JWT token
     * @throws JwtException 토큰이 유효하지 않을 경우 발생하는 예외
     */
    public void validateToken(String token) {
        Jwts.parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token);
    }
}