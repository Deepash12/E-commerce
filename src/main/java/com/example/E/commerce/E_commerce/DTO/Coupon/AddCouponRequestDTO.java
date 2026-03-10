package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCouponRequestDTO
{

    @NotNull
    @NotBlank
    private String couponCode;
    @NotNull
    private CouponType couponType;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private BigDecimal minOrderAmount;
    @NotNull
    @Positive
    private BigDecimal discountAmount;
    @NotNull
    @Positive
    private BigDecimal maximumDiscountAmount;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiryAt;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime validFrom;
    @NotNull
    private Boolean isActive = true;
    @NotNull
    @Positive
    private Integer perUserLimit;
    @NotNull
    @Positive
    private Integer globalUsageLimit;

}
