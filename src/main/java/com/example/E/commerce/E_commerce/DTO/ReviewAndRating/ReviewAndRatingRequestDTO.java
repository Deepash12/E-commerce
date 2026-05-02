package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAndRatingRequestDTO
{
    @NotBlank(message = "OrderId not be blank")
    private Long orderId;
    @NotBlank(message = "orderItemId not be blank")
    private Long orderItemId;
    @NotBlank(message = "productId not be blank")
    private Long productId;
    @NotBlank(message = "rating not be blank")
    private Integer rating;
    @NotBlank(message = "title not be blank")
    private String title;
    @NotBlank(message = "comment not be blank")
    private String comment;

}
