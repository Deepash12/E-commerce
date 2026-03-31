package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.ApiResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Service.Order.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER')")

public class OrderController
{
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ApiResponseDTO<?> checkoutOrder(@RequestBody CheckoutOrderRequestDTO dto)
    {
        CheckoutOrderResponseDTO response = orderService.checkoutOrders(dto);

        return new ApiResponseDTO<>(
                200,
                "Payment Order Created Successfully",
                LocalDateTime.now(),
                response
        );
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> checkOrderById(@PathVariable Long id,Authentication authentication)
    {
        String username= authentication.getName();
        return ResponseEntity.ok(orderService.checkOrderById(username,id));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(Authentication authentication,@PathVariable Long id)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.cancelOrder(username,id));
    }

    @GetMapping("/all")
    public Page<OrderResponseDTO> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                @RequestParam(defaultValue = "5")Integer pageSize)
    {
//        String username = authentication.getName();
        return orderService.viewOrders(pageNumber,pageSize);
    }

    @PostMapping("/place")
    public ApiResponseDTO<?> placeOrder(@RequestBody CheckoutOrderRequestDTO dto)
    {
        Order response = orderService.placeOrderAfterPayment(dto.getAddressId());

        return new ApiResponseDTO<>(
                201,
                "Order placed successfully",
                LocalDateTime.now(),
                response
        );
    }
}
