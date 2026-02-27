package com.example.E.commerce.E_commerce.DTO.Coupon;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CouponResponseUserDTO
{

        private String couponCode;

        private BigDecimal totalAmount;

        private BigDecimal discountAmount;

        private BigDecimal finalAmount;

        private String message;
}
