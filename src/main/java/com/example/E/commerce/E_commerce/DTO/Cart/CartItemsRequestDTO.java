package com.example.E.commerce.E_commerce.DTO.Cart;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemsRequestDTO
{

    @NotBlank(message = "Product Id not be null")
    private Long productId;
    @NotBlank(message = "Quantity not be null while add to cart")
    private Long quantity;

}
