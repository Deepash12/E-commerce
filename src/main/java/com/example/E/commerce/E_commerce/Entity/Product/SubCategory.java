package com.example.E.commerce.E_commerce.Entity.Product;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.awt.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "sub_category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name","category_id"})
        }
)
public class SubCategory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private String description;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
