package com.technerd.finsight.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {

    private Long userId;
    private String accessToken;
    private String refreshToken;

}
