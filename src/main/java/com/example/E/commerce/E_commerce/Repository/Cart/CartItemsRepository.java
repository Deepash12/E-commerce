package com.example.E.commerce.E_commerce.Repository.Cart;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems,Long>
{

    Optional<CartItems> findByCartAndProduct(Cart cart, Product product);
}
