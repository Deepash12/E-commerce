package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingSummaryDTO
{
    private Long totalReviews;
    private Double averageRating;
    private Map<Integer,Long> ratingBreakdown;
}
