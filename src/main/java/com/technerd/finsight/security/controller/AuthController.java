package com.technerd.finsight.security.controller;

import com.technerd.finsight.security.dto.LoginDTO;
import com.technerd.finsight.security.dto.LoginResponse;
import com.technerd.finsight.security.dto.SignUpDTO;
import com.technerd.finsight.security.dto.SignUpResponse;
import com.technerd.finsight.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    
    private final AuthService authService;

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

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpDTO signUpDTO, HttpServletResponse httpResponse) {
        SignUpResponse signUpResponse = authService.signUp(signUpDTO);

        Cookie cookie = new Cookie("REFRESH_TOKEN", signUpResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(signUpResponse);
    }

}
