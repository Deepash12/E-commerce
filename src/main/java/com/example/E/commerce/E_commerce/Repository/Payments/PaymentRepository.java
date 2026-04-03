package com.example.E.commerce.E_commerce.Repository.Payments;

import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long>
{

}
