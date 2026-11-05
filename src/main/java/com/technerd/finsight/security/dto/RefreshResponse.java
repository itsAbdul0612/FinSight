package com.technerd.finsight.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(name = "RefreshResponse", description = "New tokens issued after a successful refresh.")
public class RefreshResponse {

    @Schema(description = "Rotated refresh token set in the HTTP-only cookie", example = "eyJhbGciOi...")
    private String refreshToken;
    @Schema(description = "New bearer access token", example = "eyJhbGciOi...")
    private String accessToken;
}
