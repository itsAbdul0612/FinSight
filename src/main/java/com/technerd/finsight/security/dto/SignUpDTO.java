package com.technerd.finsight.security.dto;

import com.technerd.finsight.security.entity.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;


@Data
public class SignUpDTO {

    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
