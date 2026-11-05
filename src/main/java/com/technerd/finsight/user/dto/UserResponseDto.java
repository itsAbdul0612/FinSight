package com.technerd.finsight.user.dto;

import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "UserProfileResponse", description = "Public profile information returned by profile endpoints; excludes credentials and the internal User entity.")
public class UserResponseDto {

    @Schema(description = "User identifier", example = "12")
    private Long userId;
    @Schema(description = "Display name", example = "Alex Morgan")
    private String name;

    @Email
    @Schema(description = "Email address", example = "alex@example.com")
    private String email;

    @Schema(description = "Preferred base currency", example = "USD")
    private String baseCurrency;
    @Schema(description = "Account balance", example = "1200.00")
    private BigDecimal totalBalance;

    @Schema(description = "Account creation timestamp", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Last profile update timestamp", example = "2026-11-06T10:30:00")
    private LocalDateTime updatedAt;

}
