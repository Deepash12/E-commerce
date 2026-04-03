package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAndRatingRequestDTO
{
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Integer rating;
    private String title;
    private String comment;

}
