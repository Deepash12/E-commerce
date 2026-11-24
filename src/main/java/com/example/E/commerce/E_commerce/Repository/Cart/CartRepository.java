package com.example.E.commerce.E_commerce.Repository.Cart;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>
{
    @Modifying
    @Query("""
            DELETE FROM CartItems ci
            WHERE ci.cart.id = :cartId
            AND ci.product.id = :productId
            """)
    int RemoveItemFromCart(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId
    );

    Optional<Cart> findByUser(Users user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cart c WHERE c.id = :id")
    Optional<Cart> findByIdForUpdate(
            @Param("id") Long id
    );
}