package com.example.E.commerce.E_commerce.DTO.Cart;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemsResponseDTO
{
    private Long productId;

    private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}
