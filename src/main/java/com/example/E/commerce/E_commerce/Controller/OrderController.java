package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
import com.example.E.commerce.E_commerce.Service.Order.OrderService;
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
    private ResponseEntity<?> checkoutProduct(CheckoutOrderRequestDTO checkoutOrderRequestDTO, Authentication authentication)
    {
        String  username = authentication.getName();
        return ResponseEntity.ok(orderService.checkoutOrders(username,checkoutOrderRequestDTO));
    }

    @GetMapping("{id}")
    private ResponseEntity<?> checkOrderById(@PathVariable Long id,Authentication authentication)
    {
        String username= authentication.getName();
        return ResponseEntity.ok(orderService.checkOrderById(username,id));
    }

    @DeleteMapping("/cancel/{id}")
    private ResponseEntity<?> cancelOrder(Authentication authentication,Long id)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.cancelOrder(username,id));
    }

    @GetMapping("/all")
    private ResponseEntity<?> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNumber,
                                           @RequestParam(defaultValue = "5")Integer pageSize,
                                           Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.viewOrders(pageNumber,pageSize,username));
    }
}
