package com.example.E.commerce.E_commerce.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponseDTO
{
    List<CartItemsResponseDTO> items ;
    private BigDecimal grandTotal;
    private Integer totalItems;

    public CartResponseDTO(List<CartItemsResponseDTO> items, BigDecimal totalAmount, int totalItems)
    {
        this.items = items;
        this.grandTotal = totalAmount;
        this.totalItems = totalItems;
    }
}
