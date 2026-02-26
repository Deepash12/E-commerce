package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
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
    @Column(nullable = false)
    @NotNull
    @NotBlank@Positive
    private String couponCode;
    @Column(nullable = false)
    @NotNull
    private CouponType couponType;
    @Column(nullable = false)
    @NotNull
    @NotBlank@Positive
    private String description;
    @Column(nullable = false)
    @NotNull
    @Positive
    private BigDecimal minOrderAmount;
    @Column(nullable = false)
    @NotNull
    @Positive
    private BigDecimal discountAmount;
    @Column(nullable = false)
    @NotNull
    @Positive
    private BigDecimal maximumDiscountAmount;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime expiryAt;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime validFrom;
    @Column(nullable = false)
    @NotNull
    private Boolean isActive = true;
    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer perUserLimit;
    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer GlobalUsageLimit;

}
