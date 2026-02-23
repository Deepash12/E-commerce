package com.example.E.commerce.E_commerce.Entity.Payment;

import com.example.E.commerce.E_commerce.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory")
public class Inventory
{
    @Id
    private Long productId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer availableQuantity;

}
