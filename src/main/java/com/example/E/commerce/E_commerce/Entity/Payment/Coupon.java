package com.example.E.commerce.E_commerce.Entity.Payment;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name ="coupons")
public class Coupon
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String couponCode;
    private BigDecimal discountAmount;
    private BigDecimal minimumOrderAmount;
    private LocalDateTime expiryDate;
    @Column(nullable = false)
    private Boolean isActive = true;
}
