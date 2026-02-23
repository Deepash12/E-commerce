package com.example.E.commerce.E_commerce.Repository.Payments;

import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long>
{

    Optional<Payment> findByIdAndOrderUserUsername(Long paymentId, String username);
}
