package com.globalmed.mes.mes_api.user.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    @Value("${app.security.jwt.secret}")
    private String secretKey;

    @Value("${app.security.jwt.ttl-minutes}")
    private long ttlMinutes;

    private long validityInMs;
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        try {
            System.out.println("secretKey (base64): " + secretKey);
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            System.out.println("Decoded key length: " + keyBytes.length);
            signingKey = Keys.hmacShaKeyFor(keyBytes);
            validityInMs = ttlMinutes * 60 * 1000; // 분 → 밀리초
        } catch (Exception e) {
            System.err.println("JWT Provider 초기화 실패: " + e.getMessage());
            throw e;
        }
    }

    public String createToken(String userName, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .subject(userName)
                .claim("roles", roles) // DB에서 가져온 권한 목록
                .issuedAt(now)
                .expiration(validity)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)  // parserBuilder() 대신 parser() + verifyWith()
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    public String getUserName(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public List<String> getRoles(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }
}