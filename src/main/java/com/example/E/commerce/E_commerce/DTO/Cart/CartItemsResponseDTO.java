package com.example.E.commerce.E_commerce.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsResponseDTO
{
    private Long productId;
    private String productName;
    private BigDecimal finalPrice;
    private Integer quantity;
    private String productImageUrl;
    private BigDecimal totalPrice;
    private Boolean isActiveProduct;

    private Integer stockQuantity;
    private Boolean isAvailable;
    private String availabilityStatus;
    private String availabilityMessage;

    public CartItemsResponseDTO(
            Long productId,
            String productName,
            BigDecimal finalPrice,
            Integer quantity,
            String productImageUrl,
            BigDecimal totalPrice,
            Boolean isActiveProduct)
    {
        this.productId = productId;
        this.productName = productName;
        this.finalPrice = finalPrice;
        this.quantity = quantity;
        this.productImageUrl = productImageUrl;
        this.totalPrice = totalPrice;
        this.isActiveProduct = isActiveProduct;
    }
}
