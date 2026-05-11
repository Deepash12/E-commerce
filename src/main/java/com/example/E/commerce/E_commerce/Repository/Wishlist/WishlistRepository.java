package com.example.E.commerce.E_commerce.Repository.Wishlist;

import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
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
    Page<Wishlist> findByUser(@Param("user") Users user, Pageable pageable);


    Optional<Wishlist> findByUserAndProduct(Users user, Product product);

    List<Wishlist> findByUser(Users user);
}
