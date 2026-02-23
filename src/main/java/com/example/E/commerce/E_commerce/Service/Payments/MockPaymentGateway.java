package com.example.E.commerce.E_commerce.Service.Payments;

import com.example.E.commerce.E_commerce.Repository.Payments.PaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MockPaymentGateway implements PaymentGateway
{
    @Override
    public boolean ProcessPayment(BigDecimal amount) {
        return Math.random()>0.1;
    }

    @Override
    public boolean processPayment(BigDecimal amount) {
        return false;
    }
}
