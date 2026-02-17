package com.example.E.commerce.E_commerce.DTO.Order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO
{
    private Long orderId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderItemsResponseDTO> items;
}
