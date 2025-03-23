package com.myproject.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse implements java.io.Serializable {
    private String accessToken;
    private String refreshToken;
}
