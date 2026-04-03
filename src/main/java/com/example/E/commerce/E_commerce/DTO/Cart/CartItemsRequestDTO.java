package com.example.E.commerce.E_commerce.DTO.Cart;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemsRequestDTO
{

    private Long productId;
    private Long quantity;

}
