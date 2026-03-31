package com.example.E.commerce.E_commerce.DTO.Product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SubCategoryResponseDTO
{
    private Long id;
    private String name;
    private String description;
    private CategoryResponseDTO category;
}
