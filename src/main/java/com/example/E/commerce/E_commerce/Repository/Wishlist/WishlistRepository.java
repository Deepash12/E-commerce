package com.example.E.commerce.E_commerce.Repository.Wishlist;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Wishlist.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface WishlistRepository extends JpaRepository<Wishlist,Long>
{

    @Query("select w from Wishlist w where w.user= :user and w.product.isActive= true")
    Page<Wishlist> findByUser(@Param("user") User user, Pageable pageable);


    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    List<Wishlist> findByUser(User user);
}
