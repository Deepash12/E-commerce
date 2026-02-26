package com.example.E.commerce.E_commerce.Entity.Coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons",uniqueConstraints = {
        @UniqueConstraint(columnNames = "coupon_code")
}
)
public class Coupon
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CouponType couponType;
    @Column(unique = true,nullable = false,length = 50)
    private String couponCode ;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal minOrderAmount;
    @Column(nullable = false)
    private BigDecimal discountAmount;
    @Column(nullable = false)
    private BigDecimal maximumDiscountAmount;
    @Column(nullable = false)
    private LocalDateTime expiryAt;
    @Column(nullable = false)
    private Integer perUserLimit;
    @Column(nullable = false)
    private Integer globalUsageLimit;
    @Column(nullable = false)
    private Integer usedCount =0;
    @Column(nullable = false)
    private LocalDateTime validFrom;
    @Column(nullable = false)
    private Boolean isActive = true;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void normalizeCouponCode()
    {
        if(couponCode!=null)
        {
            couponCode = couponCode.trim().toUpperCase();
        }
    }
}
