package com.example.E.commerce.E_commerce.Repository.Order;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long>
{

    Page<Order> findByUser(User user, Pageable pageable);
    @Query("Select o from Order o where o.id= :orderId AND o.user.username= :username")
    Optional<Order> findByIdAndUsername(@Param("orderId") Long orderId,@Param("username") String username);
}
