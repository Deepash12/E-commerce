package com.example.E.commerce.E_commerce.DTO.Authorization;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDtoRequest
{
    @NotBlank
    String newPassword;
    @NotBlank
    String token;
}
