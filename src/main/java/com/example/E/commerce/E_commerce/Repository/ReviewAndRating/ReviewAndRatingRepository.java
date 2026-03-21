package com.example.E.commerce.E_commerce.Repository.ReviewAndRating;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.ReviewAndRating.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
public interface ReviewAndRatingRepository extends JpaRepository<Review,Long>
{

    Optional<Review> findByUserAndProduct(User user, Product product);

    Page<Review> findAllByProductId(Long id, Pageable pageable);

    Page<Review> findAllByUser(User user,Pageable pageable);

    Optional<Review> findByIdAndUser(Long id, User user);

    List<Review> findByProductId(Long id);

    Review findByProduct(Product product);
}
