package com.technerd.finsight.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    private Long userId;
    private String name;

    @Email
    private String email;

    private String baseCurrency;
    private BigDecimal totalBalance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
