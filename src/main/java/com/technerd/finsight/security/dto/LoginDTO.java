package com.technerd.finsight.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "LoginRequest", description = "Credentials used to authenticate an existing account.")
public class LoginDTO {

    @Email
    @Schema(description = "Account email address", example = "alex@example.com")
    private String email;
    @NotBlank
    @Schema(description = "Account password", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String password;
}
