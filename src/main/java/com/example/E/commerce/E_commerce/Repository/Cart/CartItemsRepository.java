package com.example.E.commerce.E_commerce.Repository.Cart;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository
        extends JpaRepository<CartItems, Long>
{
    Optional<CartItems> findByCartAndProduct(
            Cart cart, Product product
    );

    @Query("""
            SELECT DISTINCT ci.cart.id
            FROM CartItems ci
            WHERE ci.product.id = :productId
            ORDER BY ci.cart.id
            """)
    List<Long> findAffectedCartIds(
            @Param("productId") Long productId
    );
}