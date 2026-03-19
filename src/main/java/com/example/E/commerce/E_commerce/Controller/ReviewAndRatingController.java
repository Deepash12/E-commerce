package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.ApiResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.ReviewAndRating.*;
import com.example.E.commerce.E_commerce.Service.ReviewAndRating.ReviewAndRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reviewAndRating")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ReviewAndRatingController
{
    private final ReviewAndRatingService reviewAndRatingService;

    @PostMapping("/create")
    public ApiResponseDTO<ReviewAndRatingResponseDTO> createReviewAndRating(@RequestBody ReviewAndRatingRequestDTO reviewAndRatingRequestDTO)
    {
        ReviewAndRatingResponseDTO response = reviewAndRatingService.writeReview(reviewAndRatingRequestDTO);
        return new ApiResponseDTO<>
                (
                        201,"Review Created Successfully!!!", LocalDateTime.now(),response
                );
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponseDTO<ProductPageResponseDTO<ReviewAndRatingResponseDTO>> getAllProductReviews(@PathVariable Long id,
                                                                                               @RequestParam(defaultValue = "0") Integer pageNumber ,
                                                                                               @RequestParam(defaultValue = "5") Integer pageSize)
    {
        ProductPageResponseDTO<ReviewAndRatingResponseDTO> responseDTO = reviewAndRatingService.seeAllReviews(id,pageNumber,pageSize);
         return new ApiResponseDTO<>(201,"All Reviews",LocalDateTime.now(),responseDTO);
    }
    @GetMapping("/reviews/myReviews")
    public ApiResponseDTO<?> getAllUserReviews(@RequestParam (defaultValue = "5") Integer pageSize,@RequestParam (defaultValue = "0") Integer pageNumber)
    {
        ProductPageResponseDTO<MyReviewsResponseDTO> responseDTO =
                reviewAndRatingService.seeMyReview(pageNumber,pageSize);

        return new ApiResponseDTO<>(201,"Fetch User Reviews",LocalDateTime.now(),responseDTO);
    }

    @PutMapping("/reviews/update/{id}")
    public ApiResponseDTO<?> updateRatingAndReview(@PathVariable Long id, @RequestBody UpdateReviewAndRatingRequestDTO updateReviewAndRatingRequestDTO)
    {
        ReviewAndRatingResponseDTO responseDTO = reviewAndRatingService.updateMyReviewAndRating(id,updateReviewAndRatingRequestDTO);
        return new ApiResponseDTO<>(201,"Review updated Successfully!!!",LocalDateTime.now(),responseDTO);
    }

    @DeleteMapping("/reviews/delete/{id}")
    public ApiResponseDTO<String> removeReview(@PathVariable Long id)
    {
        reviewAndRatingService.deleteReview(id);
        return new ApiResponseDTO<>(201,"Review Deleted Successfully",LocalDateTime.now(),null);
    }

    @GetMapping("/products/{productId}/rating-summary")
    public ApiResponseDTO<?> getProductRatingSummary(@PathVariable Long productId)
    {
        RatingSummaryDTO dto = reviewAndRatingService.calculateRating(productId);
        return new ApiResponseDTO<>(201,"fetch the Product Rating",LocalDateTime.now(),dto
                );
    }

    @PutMapping("/product/update/likes/{id}")
    public ApiResponseDTO<?> updateLikeAndDislike(@PathVariable Long id , @RequestParam String action)
    {
        ReviewAndRatingResponseDTO dto = reviewAndRatingService.updateLikes(id,action);
        return new ApiResponseDTO<>(201,null,LocalDateTime.now(),dto);
    }

}
