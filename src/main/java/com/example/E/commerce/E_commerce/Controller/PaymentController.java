package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import com.example.E.commerce.E_commerce.Entity.Payment.PaymentStatus;
import com.example.E.commerce.E_commerce.Service.Payments.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController
{
//    @Autowired
//    PaymentService paymentService;

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate/{orderId}")
    private Payment initiatePayment(@PathVariable Long orderId,@RequestParam String paymentMethod )
    {
        return paymentService.initiatePayment(orderId,paymentMethod);
    }

    @PostMapping("/complete/{PaymentId}")
    private Payment completePayment(@PathVariable Long PaymentId,Authentication authentication)
    {

        return paymentService.completePayment(PaymentId);
    }
}
