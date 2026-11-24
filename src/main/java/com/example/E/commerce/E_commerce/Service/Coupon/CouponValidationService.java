
package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.AddCouponRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class CouponValidationService
{
    private final CouponRepository couponRepository;

    public CouponValidationService(CouponRepository couponRepository)
    {
        this.couponRepository = couponRepository;
    }

    public void validateForCreation(@Valid AddCouponRequestDTO request)
    {
        validateCommonFields(request);

        String couponCode = request.getCouponCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (couponRepository.existsByCouponCode(couponCode))
        {
            throw new BadRequestException(
                    "Coupon Code Already Exists!!!"
            );
        }

        if (!request.getExpiryAt().isAfter(LocalDateTime.now()))
        {
            throw new BadRequestException(
                    "Expiry date must be in the future"
            );
        }
    }

    public void validateForUpdate(
            @Valid AddCouponRequestDTO requestDTO,
            Coupon coupon)
    {
        validateCommonFields(requestDTO);

        String newCouponCode = requestDTO.getCouponCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        String existingCouponCode = coupon.getCouponCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!existingCouponCode.equals(newCouponCode))
        {
            if (couponRepository.existsByCouponCode(newCouponCode))
            {
                throw new BadRequestException(
                        "Coupon Code Already Exists!!!"
                );
            }
        }

        Integer usedCount = Optional
                .ofNullable(coupon.getUsedCount())
                .orElse(0);

        if (requestDTO.getGlobalUsageLimit() < usedCount)
        {
            throw new BadRequestException(
                    "Global usage limit cannot be less than used count"
            );
        }

        if (usedCount > 0)
        {
            if (requestDTO.getCouponType() != coupon.getCouponType())
            {
                throw new BadRequestException(
                        "Coupon type cannot be changed after usage"
                );
            }

            if (coupon.getMaximumDiscountAmount()
                    .compareTo(requestDTO.getMaximumDiscountAmount()) != 0)
            {
                throw new BadRequestException(
                        "Maximum discount amount cannot be changed after usage"
                );
            }

            if (coupon.getDiscountAmount()
                    .compareTo(requestDTO.getDiscountAmount()) != 0)
            {
                throw new BadRequestException(
                        "Discount amount cannot be changed after usage"
                );
            }

            if (coupon.getMinOrderAmount()
                    .compareTo(requestDTO.getMinOrderAmount()) != 0)
            {
                throw new BadRequestException(
                        "Minimum order amount cannot be changed after usage"
                );
            }

            if (requestDTO.getExpiryAt().isBefore(coupon.getExpiryAt()))
            {
                throw new BadRequestException(
                        "Cannot reduce expiry after usage"
                );
            }

            if (!existingCouponCode.equals(newCouponCode))
            {
                throw new BadRequestException(
                        "Coupon code cannot be changed after usage"
                );
            }
        }
    }

    private void validateCommonFields(AddCouponRequestDTO request)
    {
        if (request.getCouponCode() == null
                || request.getCouponCode().isBlank())
        {
            throw new BadRequestException(
                    "Coupon code is required"
            );
        }

        String couponCode = request.getCouponCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (couponCode.length() > 50)
        {
            throw new BadRequestException(
                    "Coupon code cannot exceed 50 characters"
            );
        }

        if (request.getCouponType() == null)
        {
            throw new BadRequestException(
                    "Coupon type is required"
            );
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank())
        {
            throw new BadRequestException(
                    "Coupon description is required"
            );
        }

        if (request.getMinOrderAmount() == null
                || request.getMinOrderAmount()
                .compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new BadRequestException(
                    "Minimum order amount must be greater than zero"
            );
        }

        if (request.getDiscountAmount() == null
                || request.getDiscountAmount()
                .compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new BadRequestException(
                    "Discount amount must be greater than zero"
            );
        }

        if (request.getMaximumDiscountAmount() == null
                || request.getMaximumDiscountAmount()
                .compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new BadRequestException(
                    "Maximum discount amount must be greater than zero"
            );
        }

        if (request.getPerUserLimit() == null
                || request.getPerUserLimit() < 1)
        {
            throw new BadRequestException(
                    "Per user limit must be at least 1"
            );
        }

        if (request.getGlobalUsageLimit() == null
                || request.getGlobalUsageLimit() < 1)
        {
            throw new BadRequestException(
                    "Global usage limit must be at least 1"
            );
        }

        if (request.getPerUserLimit() > request.getGlobalUsageLimit())
        {
            throw new BadRequestException(
                    "Per user limit cannot exceed global usage limit"
            );
        }

        if (request.getCouponType() == CouponType.PERCENTAGE)
        {
            if (request.getDiscountAmount()
                    .compareTo(BigDecimal.valueOf(100)) > 0)
            {
                throw new BadRequestException(
                        "Discount percentage cannot exceed 100"
                );
            }
        }

        if (request.getCouponType() == CouponType.FLAT)
        {
            if (request.getDiscountAmount()
                    .compareTo(request.getMaximumDiscountAmount()) != 0)
            {
                throw new BadRequestException(
                        "Flat discount must equal maximum discount amount"
                );
            }
        }

        if (request.getValidFrom() == null
                || request.getExpiryAt() == null)
        {
            throw new BadRequestException(
                    "Valid from and expiry dates are required"
            );
        }

        if (!request.getExpiryAt().isAfter(request.getValidFrom()))
        {
            throw new BadRequestException(
                    "Expiry date must be after valid from date"
            );
        }

        if (request.getIsActive() == null)
        {
            throw new BadRequestException(
                    "Coupon active status is required"
            );
        }
    }
}
