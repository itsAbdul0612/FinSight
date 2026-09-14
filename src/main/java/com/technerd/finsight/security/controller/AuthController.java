package com.technerd.finsight.security.controller;

import com.technerd.finsight.security.dto.*;
import com.technerd.finsight.security.service.AuthService;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @RequestBody SignUpDTO signUpDTO,
            HttpServletResponse httpResponse) {

        SignUpResponse signUpResponse = authService.signUp(signUpDTO);

        Cookie cookie = new Cookie("REFRESH_TOKEN", signUpResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(signUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletResponse httpResponse) {

        LoginResponse loginResponse = authService.login(loginDTO);
        Cookie cookie = new Cookie("REFRESH_TOKEN", loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/logout")
    public boolean logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthenticationServiceException("No cookie found");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow();

        authService.logout(refreshToken);
        return true;
    }


    @GetMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new AuthenticationServiceException("Cookies not found!");
        }

        String rt = Arrays.stream(cookies)
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(() -> new AuthenticationServiceException("Token Not Found"));

        RefreshResponse refreshResponse = authService.refreshTheToken(rt);
        String refreshToken = refreshResponse.getRefreshToken();

        Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(refreshResponse.getAccessToken());
    }

}
