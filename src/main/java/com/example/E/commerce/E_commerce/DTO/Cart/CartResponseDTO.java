
package com.example.E.commerce.E_commerce.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO
{
    private List<CartItemsResponseDTO> items;
    private BigDecimal grandTotal;
    private Integer totalItems;

    private String appliedCouponCode;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private Boolean hasUnavailableItems;
    private Integer unavailableItemsCount;
    private Boolean canCheckout;

    public CartResponseDTO(
            List<CartItemsResponseDTO> items,
            BigDecimal grandTotal,
            Integer totalItems)
    {
        this.items = items;
        this.grandTotal = grandTotal;
        this.totalItems = totalItems;
    }

    public CartResponseDTO(
            List<CartItemsResponseDTO> items,
            BigDecimal grandTotal,
            Integer totalItems,
            String appliedCouponCode,
            BigDecimal discountAmount,
            BigDecimal finalAmount)
    {
        this.items = items;
        this.grandTotal = grandTotal;
        this.totalItems = totalItems;
        this.appliedCouponCode = appliedCouponCode;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }
}
