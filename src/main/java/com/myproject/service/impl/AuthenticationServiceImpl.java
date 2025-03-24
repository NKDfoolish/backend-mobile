package com.myproject.service.impl;

import com.myproject.controller.request.SignInRequest;
import com.myproject.controller.response.TokenResponse;
import com.myproject.repository.UserRepository;
import com.myproject.service.AuthenticationService;
import com.myproject.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");


        return null;
    }

    @Override
    public TokenResponse getRefreshToken(String request) {
        return null;
    }
}
