package com.assistudy.userservice.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.private-key}")
	private String privateKeyBase64;
	private PrivateKey privateKey;

	@Value("${jwt.public-key}")
	private String publicKeyBase64;
	private PublicKey publicKey;

	@Value("${jwt.access-token.expiration}")
	private long jwtAccessTokenExpiration;

	@Value("${jwt.refresh-token.expiration}")
	private long jwtRefreshTokenExpiration;

	private byte[] parseKeyBytes(String keyBase64) {
		return Base64.getDecoder().decode(keyBase64.replaceAll("\\s", ""));
	}

	@PostConstruct
	public void init() throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(parseKeyBytes(privateKeyBase64));
		privateKey = keyFactory.generatePrivate(privateKeySpec);

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
			.parserBuilder()
			.setSigningKey(publicKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
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
	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	/**
	 * 만료 시간 추출
	 *
	 * @param token Json Web Token
	 * @return 만료 시간 (초)
	 */
	public long getRemainingExpirationTime(String token) {
		Date now = new Date();
		return (extractExpiration(token).getTime() - now.getTime()) / 1000;
	}

	public String generateAccessToken(Long userId) {
		String jwtId = UUID.randomUUID().toString();

		Date now = new Date();

		return Jwts.builder()
			.claim("userId", userId)
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + jwtAccessTokenExpiration))
			.setId(jwtId)
			.signWith(privateKey, SignatureAlgorithm.RS256)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		String jwtId = UUID.randomUUID().toString();
		Date now = new Date();

		return Jwts.builder()
			.claim("userId", userId)
			.setSubject("REFRESH TOKEN")
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + jwtRefreshTokenExpiration))
			.setId(jwtId)
			.signWith(privateKey, SignatureAlgorithm.RS256)
			.compact();
	}
}
