package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ReviewAndRatingResponseDTO
{
    private Long reviewId;
    private Long productId;
    private String productName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Integer like;
    private Integer dislike;
}
