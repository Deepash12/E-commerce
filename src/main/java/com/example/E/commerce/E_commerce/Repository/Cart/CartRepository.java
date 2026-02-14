package com.example.E.commerce.E_commerce.Repository.Cart;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long>
{
    @Modifying

    @Query("DELETE FROM CartItems ci \n" +
            "WHERE ci.cart.id = :cartId AND ci.product.id = :productId ")
    int RemoveItemFromCart(Long cartId,Long productId);

    Optional<Cart> findByUser(User user);
}
