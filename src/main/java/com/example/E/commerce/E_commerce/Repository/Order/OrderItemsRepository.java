package com.example.E.commerce.E_commerce.Repository.Order;

import com.example.E.commerce.E_commerce.Entity.Order.OrderItem;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository  extends JpaRepository<OrderItem,Long> {
    OrderItem findByOrderId(Long id);

    OrderItem findByProduct(Product product);
}
