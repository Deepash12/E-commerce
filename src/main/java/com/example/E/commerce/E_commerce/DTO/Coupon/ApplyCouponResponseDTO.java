package com.example.E.commerce.E_commerce.DTO.Coupon;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class ApplyCouponResponseDTO
{
    @NotBlank
    private String couponCode;
}
