package com.myproject.service;

import com.myproject.dto.request.SignInRequest;
import com.myproject.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);

    String forgotPassword(String email);
}
