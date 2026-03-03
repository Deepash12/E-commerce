package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Order.OrderResponseDTO;
import com.example.E.commerce.E_commerce.Service.Order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService orderService;

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
    private Page<OrderResponseDTO> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                @RequestParam(defaultValue = "5")Integer pageSize)
    {
//        String username = authentication.getName();
        return orderService.viewOrders(pageNumber,pageSize);
    }
}
