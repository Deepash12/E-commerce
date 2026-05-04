package com.example.E.commerce.E_commerce.Entity.Order;

import jakarta.persistence.Entity;
import lombok.Data;

public enum OrderStatus
{
    CONFIRMED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    IN_PROGRESS,
    RETURNED,
    PENDING


}
