package com.example.E.commerce.E_commerce.DTO;

import com.example.E.commerce.E_commerce.Entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO
{
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Long categoryId;
};
