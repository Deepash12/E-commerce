package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class CouponResponseDTO
{
    private Long id;
    private String couponCode;
    private String description;
    private CouponType couponType;

    private BigDecimal minOrderAmount;
    private BigDecimal discountAmount;
    private BigDecimal maximumDiscountAmount;

    private LocalDateTime validFrom;
    private LocalDateTime expiryAt;

    private Integer perUserLimit;
    private Integer globalUsageLimit;

    private Integer usedCount;

    private Boolean isActive;
    private LocalDateTime createdAt;

}
