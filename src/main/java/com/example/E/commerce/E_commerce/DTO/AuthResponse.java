package com.example.E.commerce.E_commerce.DTO;

public class AuthResponse
{
    private String accessToken;
    private String refreshToken;
    private String tokenType ="Bearer";

    public AuthResponse(String accessToken, String refreshToken) {
    }
}
