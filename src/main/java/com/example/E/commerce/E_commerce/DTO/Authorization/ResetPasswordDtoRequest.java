package com.example.E.commerce.E_commerce.DTO.Authorization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDtoRequest
{
    String newPassword;
    String token;
}
