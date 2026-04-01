package com.example.E.commerce.E_commerce.DTO.Product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO
{
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Long subcategoryId;
    private Long categoryId;
    private BigDecimal discountPrice;
};
