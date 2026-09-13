package com.technerd.finsight.security.dto;

import lombok.*;

@Data
public class SignUpResponse {

    private Long id;
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;

}
