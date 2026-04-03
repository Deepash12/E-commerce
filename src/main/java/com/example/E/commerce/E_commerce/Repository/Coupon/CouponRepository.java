package com.example.E.commerce.E_commerce.Repository.Coupon;

import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long>, JpaSpecificationExecutor<Coupon>
{
    boolean existsByCouponCode(String couponCode);

    Optional<Coupon> findByCouponCode(@NotBlank String couponCode);
}
