package com.example.E.commerce.E_commerce.Service.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@RequiredArgsConstructor
@Service
public class CouponCalculationService
{
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount)
    {
        BigDecimal discount =BigDecimal.ZERO;
        if(orderAmount.compareTo(coupon.getMinOrderAmount())<0)
        {
            throw new BadRequestException("Order Amount does not meet minimum requirement!!!");
        }

        if(coupon.getCouponType()== CouponType.Flat)
        {
            discount = coupon.getDiscountAmount();

        }
        if(coupon.getCouponType()==CouponType.Percent)
        {
            BigDecimal total = orderAmount.multiply(coupon.getDiscountAmount());
            discount = total.divide(BigDecimal.valueOf(100));
            if(discount.compareTo(coupon.getMaximumDiscountAmount()) > 0)
            {
                discount = coupon.getMaximumDiscountAmount();
            }

        }
        if(discount.compareTo(orderAmount)>0)
        {
            discount = orderAmount;
        }
        return discount;
    }
}
