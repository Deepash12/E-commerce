package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateReviewAndRatingRequestDTO
{
    private Integer rating;
    private String comment;
    private String title;
}
