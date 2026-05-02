package com.example.E.commerce.E_commerce.DTO.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckoutOrderRequestDTO
{
    @NotBlank(message = "address not be null")
    private Long addressId;
    private String couponCode;
}
