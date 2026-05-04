package com.example.E.commerce.E_commerce.Repository.ReviewAndRating;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.ReviewAndRating.Review;
import com.example.E.commerce.E_commerce.Entity.ReviewAndRating.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike,Long>
{
    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    // Count likes for a review (for recalculating after change)
    long countByReviewAndAction(Review review, String action);
}
