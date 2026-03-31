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
    private BigDecimal price;
    private Integer quantity;
    private String productImageUrl;
    private BigDecimal totalPrice;
    private Boolean isActiveProduct;

//    public CartItemsResponseDTO(Long id, String name, BigDecimal price, Integer quantity,String productImageUrl, BigDecimal multiply)
//    {
//        this.productId = id;
//        this.productName =name;
//        this.price = price;
//        this.quantity = quantity;
//        this.productImageUrl = productImageUrl;
//        this.totalPrice = multiply;
//    }
}
