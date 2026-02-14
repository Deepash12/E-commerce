package com.example.E.commerce.E_commerce.Entity.Cart;

import com.example.E.commerce.E_commerce.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItems
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "CART_ID",nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
