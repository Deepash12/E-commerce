package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
//@AllArgsConstructor
public class CouponResponseDTOForUser
{
    private Long id;
    private String couponCode;
    private String description;
    private BigDecimal discountAmount;
    private BigDecimal maximumDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime expiryAt;
    private CouponStatus calculatedStatus;
    private Boolean alreadyUsed =false;


    public CouponResponseDTOForUser(Long id, String couponCode, String description, BigDecimal discountAmount,
                                    BigDecimal maximumDiscountAmount, BigDecimal minOrderAmount, LocalDateTime expiryAt,
                                    CouponStatus calculatedStatus) {
        this.id = id;
        this.couponCode = couponCode;
        this.description = description;
        this.discountAmount = discountAmount;
        this.maximumDiscountAmount = maximumDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.expiryAt = expiryAt;
        this.calculatedStatus = calculatedStatus;
    }




}
