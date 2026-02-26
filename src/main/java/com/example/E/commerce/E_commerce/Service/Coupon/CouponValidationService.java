package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.AddCouponRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CouponValidationService
{
    private final CouponRepository couponRepository;
    public void validateForCreation(@Valid AddCouponRequestDTO request)
    {
        BigDecimal discount = request.getDiscountAmount();
        String couponCode = request.getCouponCode().trim().toUpperCase();
        if(couponRepository.existsByCouponCode(couponCode))
        {
            throw new BadRequestException("Coupon Code Already Existed!!!");
        }
        if(request.getPerUserLimit() < 1)
        {
            throw new BadRequestException("Per User Limit must be greater than 1");
        }
        if(request.getGlobalUsageLimit()<1)
        {
            throw new BadRequestException("Per Global Usage Limit must be greater than 1");
        }
        if(request.getMinOrderAmount().compareTo(BigDecimal.ZERO)<=0)
        {
            throw new BadRequestException("Minimum Order Amount should be greater than Zero!!!");
        }
        if(request.getMaximumDiscountAmount().compareTo(BigDecimal.ZERO)<=0)
        {
            throw new BadRequestException("Maximum Discount amount should be greater than 0");
        }
        if(request.getCouponType()== CouponType.Percent)
        {
            if(request.getMaximumDiscountAmount().compareTo(request.getDiscountAmount())<0)
            {
                throw new BadRequestException("Maximum Discount Amount should be greater than or equal to discount Amount");
            }
            if(request.getDiscountAmount().compareTo(BigDecimal.valueOf(100))>0)
            {
                throw new BadRequestException("Get Discount Amount should be smaller than 100%");
            }

        }
        if(request.getCouponType()==CouponType.Flat)
        {
            if(request.getDiscountAmount().compareTo(request.getMaximumDiscountAmount())!=0)
            {
                throw new BadRequestException("For Flat Discounts Maximum Discount Amount should be equal to discountAmount");
            }
        }

        if(request.getExpiryAt().isBefore(LocalDateTime.now()))
        {
            throw new BadRequestException("Expiry date cannot be in the past");
        }

        if(request.getExpiryAt().isBefore(request.getValidFrom()))
        {
            throw new BadRequestException("Expiry date must be after valid from date!!!");
        }
        if(discount.compareTo(BigDecimal.ZERO)<=0)
        {
            throw new BadRequestException("Discount should Be greater than 0");
        }
    }

    public void validateForUpdate(@Valid AddCouponRequestDTO requestDTO,Coupon coupon)
    {

        if (requestDTO.getMinOrderAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("Min order amount must be greater than zero");

        if (requestDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("Discount amount must be greater than zero");

        if (!requestDTO.getExpiryAt().isAfter(requestDTO.getValidFrom()))
            throw new BadRequestException("Expiry must be after valid from date");

        // Coupon Type validations
        if (requestDTO.getCouponType() == CouponType.Percent) {

            if (requestDTO.getDiscountAmount().compareTo(BigDecimal.valueOf(100)) > 0)
                throw new BadRequestException("Discount percent cannot exceed 100");

            if (requestDTO.getMaximumDiscountAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new BadRequestException("Maximum discount must be greater than zero");
        }

        if (requestDTO.getCouponType() == CouponType.Flat) {

            if (requestDTO.getDiscountAmount().compareTo(requestDTO.getMaximumDiscountAmount()) != 0)
                throw new BadRequestException("Flat discount must equal maximum discount");
        }

        Integer usedCount = Optional.ofNullable(coupon.getUsedCount()).orElse(0);

        if (requestDTO.getGlobalUsageLimit() < usedCount)
            throw new BadRequestException("Global usage limit cannot be less than used count");

        if (requestDTO.getPerUserLimit() > requestDTO.getGlobalUsageLimit())
            throw new BadRequestException("Per user limit cannot exceed global limit");


        if(usedCount > 0)
        {
            if(!(requestDTO.getCouponType().equals(coupon.getCouponType())))
            {
                throw new BadRequestException("Once Any user used Coupon, Coupon Type not changed");
            }
            if(coupon.getMaximumDiscountAmount().compareTo(requestDTO.getMaximumDiscountAmount())!=0)
            {
                throw new BadRequestException("Once Any user used Coupon,Maximum Discount Amount Value not changed ");
            }
            if(coupon.getDiscountAmount().compareTo(requestDTO.getDiscountAmount())!=0)
            {
                throw new BadRequestException("Once Any user used Coupon,Discount Amount Value not changed ");
            }
            if(coupon.getMinOrderAmount().compareTo(requestDTO.getMinOrderAmount())!=0)
            {
                throw new BadRequestException("Once Any user used Coupon,Minimum Order Amount Value not changed");
            }
            if(requestDTO.getExpiryAt().isBefore(coupon.getExpiryAt())) {
                throw new BadRequestException("Cannot reduce expiry after usage");
            }
            if(!coupon.getCouponCode().equals(requestDTO.getCouponCode()))
            {
                throw new BadRequestException("Coupon Code Not change,Once Coupon used!!!");
            }
        }

    }
}
