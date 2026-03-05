package com.example.E.commerce.E_commerce.DTO.Payment;

import com.example.E.commerce.E_commerce.Entity.Product.Product;

import com.example.E.commerce.E_commerce.DTO.Payment.PaymentResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class PaymentMapper {

    public PaymentResponseDTO toPaymentResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .expiresAt(payment.getExpiresAt())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .order(mapToOrderDTO(payment.getOrder()))
                .build();
    }

    private PaymentResponseDTO.OrderDTO mapToOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        return PaymentResponseDTO.OrderDTO.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .totalAmount(order.getTotalAmount())
                .status(mapToStatusDTO(order))
                .paymentStatus(order.getPaymentStatus())
                .orderItems(mapToOrderItemDTOs(order.getOrderItems()))
                .build();
    }

    private PaymentResponseDTO.StatusDTO mapToStatusDTO(Order order) {
        if (order == null || order.getStatus() == null) {
            return null;
        }

        return PaymentResponseDTO.StatusDTO.builder()
                .name(order.getStatus().name())
                .build();
    }

    private List<PaymentResponseDTO.OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return Collections.emptyList();
        }

        return orderItems.stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList());
    }

    private PaymentResponseDTO.OrderItemDTO mapToOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        // Calculate price and subtotal
        BigDecimal price = calculatePrice(orderItem);
        BigDecimal subtotal = calculateSubtotal(orderItem);

        return PaymentResponseDTO.OrderItemDTO.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .price(price)
                .subtotal(subtotal)
                .product(mapToProductDTO(orderItem.getProduct()))
                .build();
    }

    private PaymentResponseDTO.ProductDTO mapToProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        return PaymentResponseDTO.ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
    }

    private BigDecimal calculatePrice(OrderItem orderItem) {
        // If price is stored in OrderItem, use it
        // Otherwise get from product
        if (orderItem.getProduct() != null && orderItem.getProduct().getPrice() != null) {
            return orderItem.getProduct().getPrice();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateSubtotal(OrderItem orderItem) {
        BigDecimal price = calculatePrice(orderItem);
        Integer quantity = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
