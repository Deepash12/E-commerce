package com.example.E.commerce.E_commerce.DTO.Order;
import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class CheckoutOrderResponseDTO
{
    private AddressResponseDTO address;
    private BigDecimal amount;
}
