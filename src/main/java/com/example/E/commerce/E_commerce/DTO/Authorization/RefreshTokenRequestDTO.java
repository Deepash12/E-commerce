package com.example.E.commerce.E_commerce.DTO.Authorization;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO
{
    @NotBlank
    private String refreshToken;
}
