package com.example.E.commerce.E_commerce.Repository.Order;

import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import com.example.E.commerce.E_commerce.Entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long>
{

    Page<Order> findByUser(Users user, Pageable pageable);

    long countByUserIdAndCouponId(Long id, Long id1);

    List<Order> findByStatus(OrderStatus orderStatus);
}
