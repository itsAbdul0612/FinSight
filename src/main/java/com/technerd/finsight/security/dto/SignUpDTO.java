package com.technerd.finsight.security.dto;

import com.technerd.finsight.user.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;


@Data
@Schema(name = "SignUpRequest", description = "Fields used to register an account.")
public class SignUpDTO {

    @NotBlank
    @Schema(description = "Display name", example = "Alex Morgan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Email
    @Schema(description = "Account email address", example = "alex@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @NotBlank
    @Schema(description = "Account password", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
