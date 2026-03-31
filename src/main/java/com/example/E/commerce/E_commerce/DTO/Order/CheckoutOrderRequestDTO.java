package com.example.E.commerce.E_commerce.DTO.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckoutOrderRequestDTO
{
    private Long addressId;
    private String couponCode;
}
