package com.example.demo.setting.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JwtCookieUtil {

    private static final String COOKIE_NAME = "access_token";
    private static final Duration MAX_AGE = Duration.ofHours(1);
    private static final Duration ACCESS_TOKEN_MAX_AGE = Duration.ofHours(1);
    private static final String REFRESH_TOKEN_NAME = "refresh_token";

    public static ResponseCookie createAccessTokenCookie(String jwt) {
        return ResponseCookie.from(COOKIE_NAME, jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(MAX_AGE)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken, long expirationMillis) {
        return ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(expirationMillis / 1000) // 밀리초를 초로 변환
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // 즉시 삭제
                .sameSite("Lax")
                .build();
    }
}
