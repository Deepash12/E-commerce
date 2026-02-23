package com.example.E.commerce.E_commerce.Repository.Payments;

import java.math.BigDecimal;

public interface PaymentGateway
{
    boolean ProcessPayment(BigDecimal amount);

    boolean processPayment(BigDecimal amount);
}
