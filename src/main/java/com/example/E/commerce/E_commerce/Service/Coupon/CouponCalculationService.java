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
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class CouponCalculationService
{
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

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


    public Coupon validateCoupon(
            String couponCode,
            BigDecimal cartTotal,
            Long userId)
    {
        if (couponCode == null || couponCode.isBlank())
        {
            throw new BadRequestException(
                    "Coupon code is required"
            );
        }

        String normalizedCode = couponCode.trim()
                .toUpperCase(Locale.ROOT);

        Coupon coupon = couponRepository
                .findByCouponCode(normalizedCode)
                .orElseThrow(() ->
                        new BadRequestException("Invalid coupon code")
                );

        if (!Boolean.TRUE.equals(coupon.getIsActive()))
        {
            throw new BadRequestException("Coupon is not active");
        }

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getValidFrom() != null
                && now.isBefore(coupon.getValidFrom()))
        {
            throw new BadRequestException(
                    "Coupon is not yet active"
            );
        }

        if (coupon.getExpiryAt() != null
                && !now.isBefore(coupon.getExpiryAt()))
        {
            throw new BadRequestException("Coupon has expired");
        }

        if (coupon.getMinOrderAmount() != null
                && cartTotal.compareTo(
                coupon.getMinOrderAmount()
        ) < 0)
        {
            throw new BadRequestException(
                    "Minimum order amount should be ₹"
                            + coupon.getMinOrderAmount()
            );
        }

        if (coupon.getGlobalUsageLimit() != null
                && coupon.getUsedCount()
                >= coupon.getGlobalUsageLimit())
        {
            throw new BadRequestException(
                    "Coupon usage limit exceeded"
            );
        }

        long userUsage =
                orderRepository.countByUserIdAndCouponId(
                        userId, coupon.getId()
                );

        if (coupon.getPerUserLimit() != null
                && userUsage >= coupon.getPerUserLimit())
        {
            throw new BadRequestException(
                    "You have already used this coupon "
                            + "the maximum number of times"
            );
        }

        return coupon;
    }

}
