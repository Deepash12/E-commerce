package com.example.E.commerce.E_commerce.Repository.Payments;

import java.math.BigDecimal;

public interface PaymentGateway
{
    boolean processPayment(BigDecimal amount);
}
