package com.singidunum.encrypted_drive_backend.configs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access.expire}")
    private Long expireAccess;

    @Value("${jwt.refresh.expire}")
    private Long expireRefresh;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts
                .builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(generateExpirationDate(claims.get("type").toString()))
                .signWith(secretKey)
                .compact();

    }

    public Claims extractAllClaimsFromToken(String token) {
       return Jwts
               .parser()
               .verifyWith(secretKey)
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }

    public Date generateExpirationDate(String type) {
        return new Date(new Date().getTime() + (type.equals("access") ? expireAccess : expireRefresh ));
    }
}
