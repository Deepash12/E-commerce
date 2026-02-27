package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.*;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequestAdmin;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequestUser;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Cart.CartItemsRepository;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService
{
    private final CouponSpecification couponSpecification;
    private final CouponRepository couponRepository;
    private final CouponValidationService couponValidationService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
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

    public Page<getAllCouponResponseDTO> viewAllCoupon(Integer pageNumber, Integer pageSize, CouponFilterRequestAdmin filter)
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

    public Page<getAllCouponResponseDTO> viewAllActiveCoupon(Integer pageNumber, Integer pageSize)
    {
        CouponFilterRequestAdmin filterRequestUser = new CouponFilterRequestAdmin();
        filterRequestUser.setStatus(CouponStatus.ACTIVE);

        Sort sort = Sort.by(Sort.Order.asc("expiryAt"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Specification<Coupon> spec =  couponSpecification.buildSpecification(filterRequestUser);
        Page<Coupon> coupons = couponRepository.findAll(spec,pageable);
        return coupons.map(this::mapCouponToDTO);
    }

    public getAllCouponResponseDTO viewActiveCoupon(Long id)
    {
        CouponFilterRequestAdmin filterRequestUser = new CouponFilterRequestAdmin();
        filterRequestUser.setStatus(CouponStatus.ACTIVE);
        Specification<Coupon> spec = couponSpecification.buildSpecification(filterRequestUser).
                and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("id"),id));
        Coupon coupon = couponRepository.findOne(spec).orElseThrow(()-> new BadRequestException("Coupon is not Active Anymore!!!"));
        return mapCouponToDTO(coupon);
    }

    public String removeCoupon(String id)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(()-> new BadRequestException("Cart Does Not Exist!!!"));

        if(cart.getCoupon()==null)
        {
            throw new BadRequestException("No Coupon Applied");
        }
        cart.setCoupon(null);
        cartRepository.save(cart);
        return "Coupon Removed Successfully!!!";
    }

    public CouponResponseUserDTO applyCoupon(ApplyCouponResponseDTO dto)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(()-> new BadRequestException("Cart Not Found!!!"));
        if(cart.getItems().isEmpty())
        {
            throw new BadRequestException("Cart is Empty!!!");
        }
        String code = dto.getCouponCode().trim().toUpperCase();
        Coupon coupon = couponRepository.findByCouponCode(code)
                .orElseThrow(()-> new BadRequestException("Coupon Invalid!!!"));
        if(!coupon.getIsActive())
        {
            throw new BadRequestException("Coupon is not Active anymore!!!");
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getExpiryAt()))
        {
            throw new BadRequestException("Coupon is not valid at this time");
        }
        if(coupon.getUsedCount()>= coupon.getGlobalUsageLimit())
        {
            throw new BadRequestException("Coupon usage limit exceeded");
        }
        if(cart.getCoupon()!=null)
        {
            throw new BadRequestException("Coupon is Already Applied on cart,Once Removed it then Apply it");
        }

        List<CartItems> cartItems;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItems item : cart.getItems()) {

            BigDecimal price = item.getProduct().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            BigDecimal itemTotal = price.multiply(quantity);

            totalAmount = totalAmount.add(itemTotal);
        }

        if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount not satisfied");
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getCouponType() == CouponType.Flat) {

            discount = coupon.getDiscountAmount();

        } else if (coupon.getCouponType() == CouponType.Percent) {

            discount = totalAmount
                    .multiply(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100));

            if (discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
                discount = coupon.getMaximumDiscountAmount();
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discount);

        cart.setCoupon(coupon);
        cartRepository.save(cart);

        CouponResponseUserDTO response = new CouponResponseUserDTO();
        response.setCouponCode(coupon.getCouponCode());
        response.setTotalAmount(totalAmount);
        response.setDiscountAmount(discount);
        response.setFinalAmount(finalAmount);
        response.setMessage("Coupon Applied Successfully");

        return response;
    }
}
