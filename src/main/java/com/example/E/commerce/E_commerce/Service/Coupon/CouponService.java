package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.AddCouponRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.getAllCouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequest;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService
{
    private final CouponSpecification couponSpecification;
    private final CouponRepository couponRepository;
    private final CouponValidationService couponValidationService;
    public CouponResponseDTO mapToResponse(Coupon coupon)
    {
        return new CouponResponseDTO
                (
                    coupon.getId(),
                    coupon.getCouponCode(),
                    coupon.getDescription(),
                    coupon.getCouponType(),
                    coupon.getMinOrderAmount(),
                    coupon.getDiscountAmount(),
                    coupon.getMaximumDiscountAmount(),
                    coupon.getValidFrom(),
                    coupon.getExpiryAt(),
                    coupon.getPerUserLimit(),
                    coupon.getGlobalUsageLimit(),
                    coupon.getUsedCount(),
                    coupon.getIsActive(),
                    coupon.getCreatedAt()
                );
    }
    public getAllCouponResponseDTO mapCouponToDTO(Coupon coupon)
    {
        return new getAllCouponResponseDTO(
                coupon.getId(),
                coupon.getCouponCode(),
                coupon.getCouponType(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getMaximumDiscountAmount(),
                coupon.getMinOrderAmount(),
                coupon.getValidFrom(),
                coupon.getExpiryAt(),
                coupon.getPerUserLimit(),
                coupon.getGlobalUsageLimit(),
                coupon.getUsedCount(),
                calculateRemainingUsage(coupon.getGlobalUsageLimit(), coupon.getUsedCount()),
                coupon.getIsActive(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt(),
                calculateStatus(coupon)
        );
    }

    public Integer calculateRemainingUsage( Integer globalUsageLimit,Integer usedCount)
    {
        if(usedCount<globalUsageLimit)
            return globalUsageLimit-usedCount;
        else
        {
            return 0;
        }
    }

    public CouponStatus calculateStatus(Coupon coupon)
    {
        if(coupon.getIsActive()==false)
            return CouponStatus.INACTIVE;
        else if(LocalDateTime.now().isBefore(coupon.getValidFrom()))
            return CouponStatus.NOT_YET_STARTED;
        else if(LocalDateTime.now().isAfter(coupon.getExpiryAt()))
            return CouponStatus.EXPIRED;
        else if(coupon.getUsedCount()>= coupon.getGlobalUsageLimit())
            return CouponStatus.USAGE_LIMIT_REACHED;
        else
            return CouponStatus.ACTIVE;
    }


    @Transactional
    public CouponResponseDTO addCoupon(@Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        try
        {
            Coupon coupon = new Coupon();
            coupon.setCouponCode(addCouponRequestDTO.getCouponCode());
            coupon.setCouponType(addCouponRequestDTO.getCouponType());
            coupon.setDescription(addCouponRequestDTO.getDescription());
            coupon.setMinOrderAmount(addCouponRequestDTO.getMinOrderAmount());
            coupon.setDiscountAmount(addCouponRequestDTO.getDiscountAmount());
            coupon.setMaximumDiscountAmount(addCouponRequestDTO.getMaximumDiscountAmount());
            coupon.setExpiryAt(addCouponRequestDTO.getExpiryAt());
            coupon.setValidFrom(addCouponRequestDTO.getValidFrom());
            coupon.setIsActive(addCouponRequestDTO.getIsActive());
            coupon.setGlobalUsageLimit(addCouponRequestDTO.getGlobalUsageLimit());
            coupon.setPerUserLimit(addCouponRequestDTO.getPerUserLimit());
            Coupon savedCoupon = couponRepository.save(coupon);
            return mapToResponse(savedCoupon);

        } catch (DataIntegrityViolationException e)
        {
            throw new BadRequestException("Coupon code Already Exist!!!");
        }
    }
    @Transactional
    public CouponResponseDTO updateCoupon(Long id, @Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        Coupon coupon = couponRepository.findById(id).
                orElseThrow(()-> new BadRequestException("Coupon Does not Existed Anymore!!!"));
        couponValidationService.validateForUpdate(addCouponRequestDTO,coupon);
        coupon.setCouponType(addCouponRequestDTO.getCouponType());
        coupon.setDescription(addCouponRequestDTO.getDescription());
        coupon.setMinOrderAmount(addCouponRequestDTO.getMinOrderAmount());
        coupon.setDiscountAmount(addCouponRequestDTO.getDiscountAmount());
        coupon.setMaximumDiscountAmount(addCouponRequestDTO.getMaximumDiscountAmount());
        coupon.setExpiryAt(addCouponRequestDTO.getExpiryAt());
        coupon.setValidFrom(addCouponRequestDTO.getValidFrom());
        coupon.setIsActive(addCouponRequestDTO.getIsActive());
        coupon.setGlobalUsageLimit(addCouponRequestDTO.getGlobalUsageLimit());
        coupon.setPerUserLimit(addCouponRequestDTO.getPerUserLimit());
        return mapToResponse(coupon);

    }

    public Page<getAllCouponResponseDTO> viewAllCoupon(Integer pageNumber, Integer pageSize,CouponFilterRequest filter)
    {
        Sort sort = Sort.by(Sort.Order.asc("expiryAt"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Specification<Coupon> spec = couponSpecification.buildSpecification(filter);
        Page<Coupon> coupons = couponRepository.findAll(spec,pageable);
        return coupons.map(this::mapCouponToDTO);
    }

    public getAllCouponResponseDTO viewCoupon(Long id)
    {
        Coupon coupon = couponRepository.findById(id).
                orElseThrow(()-> new BadRequestException("Coupon Does Not Exist!!!"));
        return mapCouponToDTO(coupon);
    }

    @Transactional
    public String disableCoupon(Long id)
    {
        Coupon coupon = couponRepository.findById(id).
                orElseThrow(()-> new BadRequestException("Coupon does Not Exist!!!"));
        if(!coupon.getIsActive())
        {
            throw new BadRequestException("Coupon Already Disabled!!!");
        }
        if(coupon.getExpiryAt().isBefore(LocalDateTime.now()))
        {
            throw new BadRequestException("Expired Coupon Does not need to be delete !!!");
        }
        coupon.setIsActive(false);
        return "Coupon Successfully Disabled!!!";
    }
}
