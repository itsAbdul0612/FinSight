package com.technerd.finsight.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(name = "LoginResponse", description = "Authentication result returned after a successful login.")
public class LoginResponse {

    @Schema(description = "Authenticated user's identifier", example = "12")
    private Long userId;
    @Schema(description = "Bearer access token", example = "eyJhbGciOi...")
    private String accessToken;
    @Schema(description = "Refresh token; also set in an HTTP-only cookie", example = "eyJhbGciOi...")
    private String refreshToken;

}
