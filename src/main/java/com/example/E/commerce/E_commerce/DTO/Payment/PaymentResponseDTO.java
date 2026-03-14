package com.example.E.commerce.E_commerce.DTO.Payment;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDTO
{
    private Long id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;

        // Nested Order Object
    private OrderDTO order;

    @Data
    @Builder
    public static class OrderDTO {
        private Long id;
        private LocalDateTime createdAt;
        private BigDecimal totalAmount;
        private StatusDTO status;
        private PaymentStatus paymentStatus;
        private List<OrderItemDTO> orderItems;
    }

    @Data
    @Builder
    public static class StatusDTO {
        private String name;
    }

    @Data
    @Builder
    public static class OrderItemDTO {
        private Long id;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
        private ProductDTO product;
    }

    @Data
    @Builder
    public static class ProductDTO {
        private Long id;
        private String name;
    }
}

