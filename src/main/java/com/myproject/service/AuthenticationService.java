package com.myproject.service;

import com.myproject.controller.request.SignInRequest;
import com.myproject.controller.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);
}
