package com.zest.productapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

}
