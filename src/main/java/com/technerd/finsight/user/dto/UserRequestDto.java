package com.technerd.finsight.user.dto;

import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "UserProfileUpdateRequest", description = "Profile fields accepted when updating the authenticated user's profile. Does not expose the internal User entity.")
public class UserRequestDto {

    @Schema(description = "User identifier field accepted by the request model", example = "12")
    private Long userId;
    @Schema(description = "Display name", example = "Alex Morgan")
    private String name;

    @Email
    @Schema(description = "Email address", example = "alex@example.com")
    private String email;
    @Schema(description = "Password value accepted by the request model", accessMode = Schema.AccessMode.WRITE_ONLY, format = "password")
    private String password;

    @Schema(description = "Preferred base currency", example = "USD")
    private String baseCurrency;
    @Schema(description = "Account balance value accepted by the request model", example = "1200.00")
    private BigDecimal totalBalance;

    @Schema(description = "Account creation timestamp field", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Last profile update timestamp field", example = "2026-11-06T10:30:00")
    private LocalDateTime updatedAt;

}
