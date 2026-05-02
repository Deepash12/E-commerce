package com.example.E.commerce.E_commerce.DTO.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO
{
    @NotBlank(message = "name not be null")
    private String name;
    @NotBlank(message = "description not be null")
    private String description;
    @NotBlank(message = "price not be null")
    private Double price;
    @NotBlank(message = "stockQuantity not be null")
    private Integer stockQuantity;
    @NotBlank(message = "subcategoryId not be null")
    private Long subcategoryId;
    @NotBlank(message = "categoryId not be null")
    private Long categoryId;
    @NotBlank(message = "discountPrice not be null")
    private BigDecimal discountPrice;
};
