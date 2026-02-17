package com.example.E.commerce.E_commerce.Entity.Order;

import jakarta.persistence.Entity;
import lombok.Data;

public enum OrderStatus
{
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    CONFIRMED
}
