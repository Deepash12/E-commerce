package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateReviewAndRatingRequestDTO
{
    @NotBlank
    private Integer rating;
    @NotBlank
    private String comment;
    @NotBlank
    private String title;
}
