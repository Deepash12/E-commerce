package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
import com.example.E.commerce.E_commerce.Service.Order.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public OrderResponseDTO checkoutProduct(@RequestBody @Valid CheckoutOrderRequestDTO checkoutOrderRequestDTO, Authentication authentication)
    {
        return orderService.checkoutOrders(checkoutOrderRequestDTO);
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
}
