package com.example.E.commerce.E_commerce.Entity.Order;

import jakarta.persistence.Entity;
import lombok.Data;

public enum OrderStatus
{
    CREATED ,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED
    ,PENDING


}
