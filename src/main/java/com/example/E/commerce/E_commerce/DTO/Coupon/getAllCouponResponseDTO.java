package com.example.E.commerce.E_commerce.DTO.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class getAllCouponResponseDTO
{

    private Long id;
    private String couponCode;
    private CouponType couponType;
    private String description;
    private BigDecimal discountAmount;
    private BigDecimal maximumDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime validFrom;
    private LocalDateTime expiryAt;
    private Integer perUserLimit;
    private Integer globalUsageLimit;
    private Integer usedCount;
    private Integer remainingUsage;//(globalUsageLimit - usedCount)
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CouponStatus calculatedStatus;

}
