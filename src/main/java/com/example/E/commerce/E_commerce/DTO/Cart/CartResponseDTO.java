package com.example.E.commerce.E_commerce.DTO.Cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponseDTO
{
    List<CartItemsResponseDTO> items ;
    private BigDecimal grandTotal;
    private Integer totalItems;
}
