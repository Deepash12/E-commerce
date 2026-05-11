package com.example.E.commerce.E_commerce.Entity.ReviewAndRating;

import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "review_likes",
        uniqueConstraints = {
                // ✅ One row per user per review — database level enforcement
                @UniqueConstraint(columnNames = {"user_id", "review_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Which review
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // "LIKE" or "DISLIKE"
    @Column(nullable = false)
    private String action;
}
