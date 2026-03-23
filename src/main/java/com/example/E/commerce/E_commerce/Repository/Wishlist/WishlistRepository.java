package com.example.E.commerce.E_commerce.Repository.Wishlist;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Wishlist.Wishlist;
import io.micrometer.common.KeyValues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist,Long>
{

    Boolean deleteProductById(Long id);

    Page<Wishlist> findByUser(User user, Pageable pageable);


    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    List<Wishlist> findByUser(User user);
}
