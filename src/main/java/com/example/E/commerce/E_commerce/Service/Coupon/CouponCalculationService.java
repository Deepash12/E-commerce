package com.example.E.commerce.E_commerce.Service.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CouponCalculationService
{
    private final CouponRepository couponRepository;

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount)
    {
        BigDecimal discount = BigDecimal.ZERO;

        if (orderAmount.compareTo(coupon.getMinOrderAmount()) < 0)
        {
            throw new BadRequestException("Order amount does not meet minimum requirement");
        }

        if (coupon.getCouponType() == CouponType.FLAT)
        {

            discount = coupon.getDiscountAmount();

        }
        else if (coupon.getCouponType() == CouponType.PERCENTAGE)
        {
            discount = orderAmount
                    .multiply(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaximumDiscountAmount() != null &&
                    discount.compareTo(coupon.getMaximumDiscountAmount()) > 0)
            {

                discount = coupon.getMaximumDiscountAmount();
            }
        }

        if (discount.compareTo(orderAmount) > 0)
        {
            discount = orderAmount;
        }

        return discount;
    }


        public Coupon validateCoupon(String couponCode, BigDecimal cartTotal) {

        // 1. Check null / empty
        if (couponCode == null || couponCode.trim().isEmpty()) {
            throw new BadRequestException("Coupon code is required");
        }

        // 2. Find coupon
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));

        // 3. Check active
        if (!coupon.getIsActive()) {
            throw new BadRequestException("Coupon is not active");
        }

        // 4. Check expiry
        if (coupon.getExpiryAt() != null &&
                coupon.getExpiryAt().isBefore(LocalDateTime.now())) {

            throw new BadRequestException("Coupon has expired");
        }

        // 5. Check minimum order amount
        if (coupon.getMinOrderAmount() != null &&
                cartTotal.compareTo(coupon.getMinOrderAmount()) < 0) {

            throw new BadRequestException(
                    "Minimum order amount should be ₹" + coupon.getMinOrderAmount()
            );
        }

        // 6. (Optional) usage limit
        if (coupon.getGlobalUsageLimit() != null &&
                coupon.getUsedCount() >= coupon.getGlobalUsageLimit()) {

            throw new BadRequestException("Coupon usage limit exceeded");
        }

        return coupon;
    }

}
