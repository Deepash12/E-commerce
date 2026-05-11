package com.example.E.commerce.E_commerce.Service.ReviewAndRating;

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.ReviewAndRating.*;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.ReviewAndRating.Review;
import com.example.E.commerce.E_commerce.Entity.ReviewAndRating.ReviewLike;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Order.OrderItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.ReviewAndRating.ReviewAndRatingRepository;
import com.example.E.commerce.E_commerce.Repository.ReviewAndRating.ReviewLikeRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewAndRatingService
{
    private final ReviewAndRatingRepository reviewAndRatingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    private ReviewAndRatingResponseDTO mapTOReviewDTO(Review review)
    {
        return new ReviewAndRatingResponseDTO
                (
                        review.getId(),
                        review.getProduct().getId(),
                        review.getProduct().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt(),
                        review.getLikes(),
                        review.getDislikes(),
                        review.getUser()
                );
    }

    private MyReviewsResponseDTO mapTOMyReviewDTO(Review review)
    {
        return MyReviewsResponseDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .likes(review.getLikes())
                .rating(review.getRating())
                .dislikes(review.getDislikes())
                .comment(review.getComment())
                .verified(review.isVerified())
                .createdAt(review.getCreatedAt())
                .user(mapToUserDTO(review.getUser()))
                .product(mapToProductDTO(review.getProduct()))
                .build();

    }
    private UserResponseDTO mapToUserDTO(Users user)
    {
        return UserResponseDTO.builder()
                .id(user.getId()).username(user.getUsername()).avatar_url(user.getAvatar_url()).build();
    }

    private ProductResponseReviewDTO mapToProductDTO(Product product)
    {
        return ProductResponseReviewDTO.builder()
                .id(product.getId())
                .averageRating(product.getAverageRating())
                .price(product.getPrice())
                .productImageUrl(product.getProductImageUrl())
                .reviewCount(product.getReviewCount())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
                .build();
    }

    public ReviewAndRatingResponseDTO writeReview(ReviewAndRatingRequestDTO requestDTO)
    {

        OrderItem item = orderItemsRepository.findById(requestDTO.getOrderItemId())
                .orElseThrow(() -> new BadRequestException("Order item not found"));


        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if(!item.getOrder().getUser().getId().equals(user.getId()))
        {
            throw new BadRequestException("Unauthorized review attempt");
        }

        Order order = item.getOrder();

        if(order.getStatus() != OrderStatus.DELIVERED)
        {
            throw new BadRequestException("Review allowed only after delivery");
        }

        Product product = item.getProduct();

        Optional<Review> existingReview =
                reviewAndRatingRepository.findByUserAndProduct(user, product);

        if(existingReview.isPresent())
        {
            throw new BadRequestException("Review already exists for this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment());
        review.setTitle(requestDTO.getTitle());
        review.setCreatedAt(LocalDateTime.now());
        review.setVerified(true);
        reviewAndRatingRepository.save(review);
        updateProductRating(product.getId());
        return mapTOReviewDTO(review);

    }

    public ProductPageResponseDTO<ReviewAndRatingResponseDTO> seeAllReviews(Long id,Integer pageNumber,Integer pageSize)
    {
        Pageable pageable =  PageRequest.of(pageNumber,pageSize, Sort.by("createdAt").descending());

        Page<Review> review = reviewAndRatingRepository.findAllByProductId(id,pageable);

        List<ReviewAndRatingResponseDTO> dtoList = review.getContent().stream().map(this::mapTOReviewDTO)
                .toList();
        ProductPageResponseDTO<ReviewAndRatingResponseDTO> dto = new ProductPageResponseDTO<>();
        dto.setContent(dtoList);
        dto.setTotalPages(review.getTotalPages());
        dto.setTotalElements(review.getTotalElements());
        dto.setPageSize(review.getSize());
        dto.setCurrentPage(review.getNumber());
        dto.setLast(review.isLast());
        return dto;
    }

    public ProductPageResponseDTO<MyReviewsResponseDTO> seeMyReview(Integer pageNumber, Integer pageSize)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewAndRatingRepository.findAllByUser(user,pageable);
        List<MyReviewsResponseDTO> dtoList = reviews.getContent().stream().map(this::mapTOMyReviewDTO).toList();
        ProductPageResponseDTO<MyReviewsResponseDTO> dto = new ProductPageResponseDTO<>();
        dto.setContent(dtoList);
        dto.setTotalPages(reviews.getTotalPages());
        dto.setTotalElements(reviews.getTotalElements());
        dto.setPageSize(reviews.getSize());
        dto.setCurrentPage(reviews.getNumber());
        dto.setLast(reviews.isLast());
        return dto;

    }

    public ReviewAndRatingResponseDTO updateMyReviewAndRating(Long id, UpdateReviewAndRatingRequestDTO updatedto)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Review existinngReview = reviewAndRatingRepository.findByIdAndUser(id,user)
                .orElseThrow(()-> new BadRequestException("Review Not Existed"));

        existinngReview.setComment(updatedto.getComment());
        existinngReview.setRating(updatedto.getRating());
        existinngReview.setTitle(updatedto.getTitle());
        ReviewAndRatingResponseDTO response = mapTOReviewDTO(reviewAndRatingRepository.save(existinngReview));
        updateProductRating(existinngReview.getProduct().getId());
        return response;
    }

    @Transactional
    public String deleteReview(Long id)
    {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Review review = reviewAndRatingRepository.findByIdAndUser(id,user)
                .orElseThrow(()->new BadRequestException("Review Not Found!!!"));
        Product product = review.getProduct();

        reviewAndRatingRepository.delete(review);
        updateProductRating(product.getId());
        return "Review Deleted Successfully";
    }

    public RatingSummaryDTO calculateRating(Long id)
    {
        List<Review> reviews = reviewAndRatingRepository.findByProductId(id);

        long totalReviews = reviews.size();

        double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Map<Integer,Long> ratingCount = reviews.stream()
                .collect(Collectors.groupingBy(
                        Review::getRating,Collectors.counting()
                        )
                );
        Map<Integer,Long> finalBreakdown = new HashMap<>();
        for(int i=1;i<=5;i++)
        {
            finalBreakdown.put(i,ratingCount.getOrDefault(i,0L));
        }
        return new RatingSummaryDTO(totalReviews,averageRating,finalBreakdown);

    }

    private void updateProductRating(Long productId)
    {
        List<Review> reviews = reviewAndRatingRepository.findByProductId(productId);

        int totalReviews = reviews.size();

        if (totalReviews == 0)
        {
            Product product = productRepository.findById(productId)
                    .orElseThrow();

            product.setAverageRating(BigDecimal.ZERO);
            product.setReviewCount(0);

            productRepository.save(product);
            return;
        }

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Product product = productRepository.findById(productId)
                .orElseThrow();
        product.setAverageRating(BigDecimal.valueOf(avg));
        product.setReviewCount(totalReviews);
        productRepository.save(product);
    }


    public ReviewAndRatingResponseDTO updateLikes(Long id, String action)
    {

        Review review = reviewAndRatingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review Not Exist!!!"));

        // 2. Get the currently logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        // 3. Check if this user already reacted to this review
        Optional<ReviewLike> existingReaction = reviewLikeRepository.findByUserAndReview(user, review);

        if (existingReaction.isPresent()) {
            ReviewLike existing = existingReaction.get();

            if (existing.getAction().equalsIgnoreCase(action)) {
                // ─── Same action again (e.g. liked → liked again) ───────────────
                // UNDO the reaction (toggle off)
                reviewLikeRepository.delete(existing);

                if (action.equalsIgnoreCase("LIKE")) {
                    review.setLikes(Math.max(0, review.getLikes() - 1));
                } else {
                    review.setDislikes(Math.max(0, review.getDislikes() - 1));
                }

            } else {
                // ─── Switched reaction (e.g. liked → disliked) ──────────────────
                existing.setAction(action.toUpperCase());
                reviewLikeRepository.save(existing);

                if (action.equalsIgnoreCase("LIKE")) {
                    review.setLikes(review.getLikes() + 1);
                    review.setDislikes(Math.max(0, review.getDislikes() - 1));
                } else {
                    review.setDislikes(review.getDislikes() + 1);
                    review.setLikes(Math.max(0, review.getLikes() - 1));
                }
            }

        } else {
            // ─── First time reacting ─────────────────────────────────────────────
            ReviewLike newReaction = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .action(action.toUpperCase())
                    .build();
            reviewLikeRepository.save(newReaction);

            if (action.equalsIgnoreCase("LIKE")) {
                review.setLikes(review.getLikes() + 1);
            } else {
                review.setDislikes(review.getDislikes() + 1);
            }
        }

        reviewAndRatingRepository.save(review);
        return mapTOReviewDTO(review);


    }
}

//        Review review = reviewAndRatingRepository.findById(id).orElseThrow(()-> new BadRequestException("Review Not Exist!!!"));
//        if(Objects.equals(action, "LIKE") || Objects.equals(action, "like"))
//        {
//            review.setLikes(review.getLikes()+1);
//            reviewAndRatingRepository.save(review);
//        }
//        if(Objects.equals(action,"DISLIKE")||Objects.equals(action,"dislike"))
//        {
//            review.setDislikes(review.getDislikes()+1);
//            reviewAndRatingRepository.save(review);
//        }
//        return mapTOReviewDTO(review);

//        if (action.equalsIgnoreCase("LIKE")) {
//            review.setLikes(review.getLikes() + 1);
//        } else if (action.equalsIgnoreCase("DISLIKE")) {
//            review.setDislikes(review.getDislikes() + 1);
//        }
//        reviewAndRatingRepository.save(review); // save once, outside the if blocks
//        return mapTOReviewDTO(review);
