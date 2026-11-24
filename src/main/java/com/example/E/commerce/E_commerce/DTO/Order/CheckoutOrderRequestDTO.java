package com.example.E.commerce.E_commerce.DTO.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckoutOrderRequestDTO
{
    @NotNull(message = "Address is required")
    @Positive(message = "Invalid address ID")
    private Long addressId;

    private String couponCode;
}