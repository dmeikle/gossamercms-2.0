package com.gossamercms.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtService(String secret, long ttlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlSeconds = ttlSeconds;
    }

    // =========================
    // TOKEN GENERATION
    // =========================
    public String generateToken(UUID userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("decayDate", exp.toEpochMilli());

        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // TOKEN PARSING (RAW)
    // =========================
    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(300)
                .build()
                .parseSignedClaims(token);
    }
}