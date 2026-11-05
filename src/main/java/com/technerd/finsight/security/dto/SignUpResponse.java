package com.technerd.finsight.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(name = "SignUpResponse", description = "Account summary and authentication tokens returned after registration.")
public class SignUpResponse {

    @Schema(description = "New account identifier", example = "12")
    private Long id;
    @Schema(description = "Display name", example = "Alex Morgan")
    private String name;
    @Schema(description = "Account email address", example = "alex@example.com")
    private String email;
    @Schema(description = "Bearer access token", example = "eyJhbGciOi...")
    private String accessToken;
    @Schema(description = "Refresh token; also set in an HTTP-only cookie", example = "eyJhbGciOi...")
    private String refreshToken;

}
