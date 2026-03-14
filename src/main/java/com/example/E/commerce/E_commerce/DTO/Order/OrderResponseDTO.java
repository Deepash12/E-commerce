package com.example.E.commerce.E_commerce.DTO.Order;

import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponDtoOrderResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDTO
{
    private Long id;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryDate;
    private String CancelReason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<OrderItemsResponseDTO> items;
    private BigDecimal discountAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CouponDtoOrderResponse coupon;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AddressResponseDTO address;

}
