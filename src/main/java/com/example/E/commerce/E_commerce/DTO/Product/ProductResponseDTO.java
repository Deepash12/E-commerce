package com.example.E.commerce.E_commerce.DTO.Product;

import com.example.E.commerce.E_commerce.Entity.Product.Category;
import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class ProductResponseDTO
{
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private SubCategory subCategoryName;
    private Category categoryName;
    private BigDecimal discountPrice;
    private String productImageUrl;
    private Boolean isWishlist;
    private LocalDateTime createdAt;
}
