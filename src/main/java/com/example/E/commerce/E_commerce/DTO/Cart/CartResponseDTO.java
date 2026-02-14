package com.example.E.commerce.E_commerce.DTO.Cart;

import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponseDTO
{
    List<CartItemsResponseDTO> items ;
    private Double grandTotal;
    private Integer totalItems;
}
