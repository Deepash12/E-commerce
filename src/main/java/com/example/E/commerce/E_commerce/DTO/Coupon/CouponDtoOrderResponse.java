package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDtoOrderResponse
{
    private String couponCode;
    private BigDecimal discountAmount;
    private String minimumAmount;
}
