package com.example.E.commerce.E_commerce.DTO.ReviewAndRating;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserResponseDTO
{
    private Long id;
    private String username;
    private String avatar_url;

}
