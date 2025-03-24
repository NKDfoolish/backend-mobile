package com.myproject.service;

import com.myproject.common.TokenType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface JwtService {

    String generateAccessToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String extractUsername(String token, TokenType type);

}
