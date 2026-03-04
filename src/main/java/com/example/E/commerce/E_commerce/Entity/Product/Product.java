package com.example.E.commerce.E_commerce.Entity.Product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Name;
    private String Description;
    private BigDecimal price;
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id",nullable = false)
    private SubCategory subCategory;


//    private Category category;
@Column(name = "created_at", insertable = false, updatable = false)
private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    private Timestamp deletedAt;

//    private boolean active;
}
