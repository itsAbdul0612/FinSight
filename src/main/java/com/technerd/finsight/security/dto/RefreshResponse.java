package com.technerd.finsight.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshResponse {

    private String refreshToken;
    private String accessToken;
}
