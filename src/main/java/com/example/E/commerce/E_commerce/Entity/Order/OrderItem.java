package com.example.E.commerce.E_commerce.Entity.Order;

import com.example.E.commerce.E_commerce.Entity.Product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)

    private Product product;

    private String productName;
//    private BigDecimal productPrice;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

}
