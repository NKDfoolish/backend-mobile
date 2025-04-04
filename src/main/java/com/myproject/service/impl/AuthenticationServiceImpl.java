package com.myproject.service.impl;

import com.myproject.dto.request.SignInRequest;
import com.myproject.dto.response.TokenResponse;
import com.myproject.exception.ForBiddenException;
import com.myproject.exception.InvalidDataException;
import com.myproject.model.UserEntity;
import com.myproject.repository.UserRepository;
import com.myproject.service.AuthenticationService;
import com.myproject.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.myproject.common.TokenType.REFRESH_TOKEN;

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

        List<String> authorities = new ArrayList<>();

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthentication: {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities());
            authorities.add(authentication.getAuthorities().toString());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException | DisabledException e) {
            log.error("Login failed: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), authorities);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        try {
            // Verify token
            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

            // check user is active or inactivated
            UserEntity user = userRepository.findByUsername(userName);
            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));


            // generate new access token
            String accessToken = jwtService.generateAccessToken(user.getUsername(), authorities);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForBiddenException(e.getMessage());
        }
    }

    @Override
    public String forgotPassword(String email) {
        log.info("Forgot password");
        // check email exist or not

        // User is active or inactivated

        // generate reset password token

        // send email to user with reset password link

        return "";
    }

}
