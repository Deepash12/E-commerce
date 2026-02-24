package com.example.E.commerce.E_commerce.Repository.Wishlist;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Wishlist.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist,Long>
{

    Boolean deleteProductById(Long id);

    Page<Wishlist> findByUser(User user, Pageable pageable);

    boolean existsByUserAndProduct(User user, Product product);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);
}
