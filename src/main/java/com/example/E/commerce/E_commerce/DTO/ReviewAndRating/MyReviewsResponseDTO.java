package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MyReviewsResponseDTO
{
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProductResponseReviewDTO product;
    private Integer rating;
    private String title;
    private String comment;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponseDTO user;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer dislikes;
    private Boolean verified;
}
