package com.example.E.commerce.E_commerce.Entity.Payment;

import com.example.E.commerce.E_commerce.Entity.Order.Order;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data

@Table(name = "payments")
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    private String transactionId;
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String paymentMethod;
    private String failureReason;
    private Integer failureCount;
    @Version
    private Long version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;

    // Fields reserved for future real payment gateway integration
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;

}
