package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Order.CheckoutOrderResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Payment.PaymentMapper;
import com.example.E.commerce.E_commerce.DTO.Payment.PaymentResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentMethod;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Service.Payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping("/initiate")
    private ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody CheckoutOrderResponseDTO dto, @RequestParam PaymentMethod paymentMethod )
    {
        PaymentResponseDTO response = paymentService.initiatePayment(dto,paymentMethod);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete/{paymentId}")
    private ResponseEntity<PaymentResponseDTO> completePayment(@PathVariable Long paymentId)

    {
        Payment payment = paymentService.completePayment(paymentId);
        PaymentResponseDTO response = paymentMapper.toPaymentResponse(payment);
        return ResponseEntity.ok(response);
    }
}
