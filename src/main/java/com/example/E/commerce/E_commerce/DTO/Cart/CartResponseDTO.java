//package com.example.E.commerce.E_commerce.DTO.Cart;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class CartResponseDTO
//{
//    List<CartItemsResponseDTO> items ;
//    private BigDecimal grandTotal;
//    private Integer totalItems;
//
//    public CartResponseDTO(List<CartItemsResponseDTO> items, BigDecimal totalAmount, int totalItems)
//    {
//        this.items = items;
//        this.grandTotal = totalAmount;
//        this.totalItems = totalItems;
//    }
//}

package com.example.E.commerce.E_commerce.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponseDTO {

    List<CartItemsResponseDTO> items;
    private BigDecimal grandTotal;
    private Integer totalItems;

    // ✅ Yeh 3 fields add karo
    private String appliedCouponCode;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    // Existing constructor (bina coupon ke — purana code break na ho)
    public CartResponseDTO(List<CartItemsResponseDTO> items,
                           BigDecimal totalAmount,
                           int totalItems) {
        this.items = items;
        this.grandTotal = totalAmount;
        this.totalItems = totalItems;
    }

    // ✅ Naya constructor — coupon ke saath
    public CartResponseDTO(List<CartItemsResponseDTO> items,
                           BigDecimal grandTotal,
                           int totalItems,
                           String appliedCouponCode,
                           BigDecimal discountAmount,
                           BigDecimal finalAmount) {
        this.items = items;
        this.grandTotal = grandTotal;
        this.totalItems = totalItems;
        this.appliedCouponCode = appliedCouponCode;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }
}
