package com.example.E.commerce.E_commerce.Repository.Order;

import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository  extends JpaRepository<OrderItem,Long> {
}
