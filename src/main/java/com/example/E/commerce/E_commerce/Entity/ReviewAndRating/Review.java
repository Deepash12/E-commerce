package com.example.E.commerce.E_commerce.Entity.ReviewAndRating;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","product_id"}))
public class Review
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer rating;

    @Column(length = 1000)
    private String comment ;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem item;

    private Integer likes =0;

    private Integer dislikes=0;
    private String title;
    private boolean verified=false;


}
