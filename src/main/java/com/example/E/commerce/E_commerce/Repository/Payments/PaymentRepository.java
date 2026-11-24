package com.example.E.commerce.E_commerce.Repository.Payments;

import com.example.E.commerce.E_commerce.Entity.Payment.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long>
{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdForUpdate(
            @Param("id") Long id
    );
}