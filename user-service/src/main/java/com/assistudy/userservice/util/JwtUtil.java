package com.assistudy.userservice.util;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.private-key-path}")
	private String privateKeyPath;
	private PrivateKey privateKey;

	@Value("${jwt.public-key-path}")
	private String publicKeyPath;
	private PublicKey publicKey;

	@Value("${jwt.access-token.expiration}")
	private long jwtAccessTokenExpiration;

	@Value("${jwt.refresh-token.expiration}")
	private long jwtRefreshTokenExpiration;

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

		// Private Key Load (PKCS#8 형식)
		// file: 프로토콜 제거하고 FileSystemResource 사용
		String privateKeyFilePath = privateKeyPath.replace("file:", "");
		Resource privateKeyResource = new FileSystemResource(privateKeyFilePath);
		byte[] privateKeyBytes = readKeyBytes(privateKeyResource);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		privateKey = keyFactory.generatePrivate(privateKeySpec);

		// Public Key Load (X.509 형식)
		// file: 프로토콜 제거하고 FileSystemResource 사용
		String publicKeyFilePath = publicKeyPath.replace("file:", "");
		Resource publicKeyResource = new FileSystemResource(publicKeyFilePath);
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
