package com.myproject.controller;

import com.myproject.dto.response.ApiResponse;
import com.myproject.dto.request.SignInRequest;
import com.myproject.dto.response.TokenResponse;
import com.myproject.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_CONTROLLER")
@Tag(name = "Authentication Controller", description = "APIs for authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token", description = "Get access token and refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request) {
        log.info("Access token request");

        return authenticationService.getAccessToken(request);
    }

    @Hidden
    @Operation(summary = "Refresh token", description = "Get new access token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");

        return authenticationService.getRefreshToken(refreshToken);
    }

    @Hidden
    @Operation(summary = "Forgot password", description = "Send email to reset password")
    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword(@RequestBody String email) {
        log.info("Forgot password request");

        return ApiResponse.builder()
                .status(200)
                .message("Password reset link sent to email")
                .data(null)
                .build();
    }
}
