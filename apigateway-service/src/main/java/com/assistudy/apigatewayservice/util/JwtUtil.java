package com.assistudy.apigatewayservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;
    private PublicKey publicKey;

    /**
     * Key 파일 읽어 실제 데이터 추출.
     */
    private byte[] readKeyBytes(Resource keyFile) throws IOException {

        String keyContent = new String(keyFile.getInputStream().readAllBytes());

        // PEM 형식의 헤더와 푸터 제거
        keyContent = keyContent.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        // Base64 디코딩
        return java.util.Base64.getDecoder().decode(keyContent);
    }

    @PostConstruct
    public void init() throws Exception {
        // RSA 형식 지정
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Public Key Load (X.509 형식)
        // 환경에 따라 파일 경로 처리
        Resource publicKeyResource;
        try {
            // 먼저 상대 경로로 시도 (개발 환경)
            publicKeyResource = new FileSystemResource("./native-config-repo/" + publicKeyPath);
            if (!publicKeyResource.exists()) {
                // 절대 경로로 시도 (프로덕션 환경)
                publicKeyResource = new FileSystemResource("/app/native-config-repo/" + publicKeyPath);
            }
        } catch (Exception e) {
            // 마지막으로 현재 디렉토리에서 시도
            publicKeyResource = new FileSystemResource(publicKeyPath);
        }
        
        byte[] publicKeyBytes = readKeyBytes(publicKeyResource);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
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