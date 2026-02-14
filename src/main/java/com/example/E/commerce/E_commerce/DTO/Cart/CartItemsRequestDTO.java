package com.example.E.commerce.E_commerce.DTO.Cart;

import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
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
