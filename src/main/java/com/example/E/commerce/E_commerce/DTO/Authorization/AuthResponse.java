package com.example.E.commerce.E_commerce.DTO.Authorization;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse
{
    private String accessToken;
    private String refreshToken;
    private String tokenType ="Bearer";

    public AuthResponse(String accessToken, String refreshToken) {
    }
}
