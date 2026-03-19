package com.example.E.commerce.E_commerce.DTO.Order;

import lombok.*;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class OrderItemsResponseDTO
{
    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private String productImageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;


}
