package com.technerd.finsight.security.controller;

import com.technerd.finsight.security.dto.*;
import com.technerd.finsight.security.service.AuthService;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Arrays;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication", description = "Account registration, login, and JWT renewal.")
public class AuthController {

    private final AuthService authService;
    private static final String RT = "REFRESH_TOKEN";

    @PostMapping("/sign-up")
    @Operation(summary = "Register an account", description = "Creates an account and returns access credentials. The refresh token is also set as an HTTP-only cookie.")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @RequestBody SignUpDTO signUpDTO,
            HttpServletResponse httpResponse) {

        SignUpResponse signUpResponse = authService.signUp(signUpDTO);

        Cookie cookie = new Cookie(RT, signUpResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(signUpResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates the supplied credentials and returns access credentials. The refresh token is also set as an HTTP-only cookie.")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletResponse httpResponse) {

        LoginResponse loginResponse = authService.login(loginDTO);
        Cookie cookie = new Cookie(RT, loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false); // Just to remember.
        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/refresh")
    @Operation(summary = "Refresh access credentials", description = "Reads the refresh token from the REFRESH_TOKEN cookie, rotates it, and returns a new access token as plain text.")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new AuthenticationServiceException("Cookies not found!");
        }

        String rt = Arrays.stream(cookies)
                .filter(cookie -> RT.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue).orElseThrow(() -> new AuthenticationServiceException("Token Not Found"));

        RefreshResponse refreshResponse = authService.refreshTheToken(rt);
        String refreshToken = refreshResponse.getRefreshToken();

        Cookie cookie = new Cookie(RT, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);

        return ResponseEntity.ok(refreshResponse.getAccessToken());
    }

}
