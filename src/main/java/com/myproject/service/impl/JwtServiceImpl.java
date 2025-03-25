package com.myproject.service.impl;

import com.myproject.common.TokenType;
import com.myproject.exception.InvalidDataException;
import com.myproject.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT_SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDays}")
    private long expiryDays;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("Generate access token with username: {}, authorities: {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateAccess(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("Generate refresh token with username: {}, authorities: {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateRefresh(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("Extract username from token: {}, type: {}", token, type);

        return extractClaim(type, token, Claims::getSubject);
    }

    private <T> T extractClaim(TokenType type, String token, Function<Claims, T> clamExtractor){
        final Claims claims = extractAllClaims(type, token);
        return clamExtractor.apply(claims);
    }

    private Claims extractAllClaims(TokenType type, String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getKey(type))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied!, error: " + e.getMessage());
        }
    }

    private String generateAccess(Map<String, Object> claims, String username){
        log.info("Generate token with claims: {}, username: {}", claims, username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefresh(Map<String, Object> claims, String username){
        log.info("Generate token with claims: {}, username: {}", claims, username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDays))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type){
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> throw new InvalidDataException("Invalid token type");
        }
    }
}
