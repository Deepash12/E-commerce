package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.*;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequestAdmin;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Cart.Cart;
import com.example.E.commerce.E_commerce.Entity.Cart.CartItems;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Cart.CartRepository;
import com.example.E.commerce.E_commerce.Repository.Coupon.CouponRepository;
import com.example.E.commerce.E_commerce.Repository.Order.OrderRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService
{
    private final CouponSpecification couponSpecification;
    private final CouponRepository couponRepository;
    private final CouponValidationService couponValidationService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    public CouponService(CouponSpecification couponSpecification, CouponRepository couponRepository, CouponValidationService couponValidationService, UserRepository userRepository, CartRepository cartRepository, OrderRepository orderRepository) {
        this.couponSpecification = couponSpecification;
        this.couponRepository = couponRepository;
        this.couponValidationService = couponValidationService;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

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

    public CouponResponseDTOForUser mapCouponToDTOForUser(Coupon coupon)
    {
        return new CouponResponseDTOForUser
                (
                        coupon.getId(),
                        coupon.getCouponCode(),
                        coupon.getDescription(),
                        coupon.getDiscountAmount(),
                        coupon.getMaximumDiscountAmount(),
                        coupon.getMinOrderAmount(),
                        coupon.getExpiryAt(),
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
            System.out.println(addCouponRequestDTO.getCouponCode());
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
    public String toggleCouponStatus(Long id)
    {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Coupon Not Found!!!"));

        // If coupon is currently ACTIVE → disable it
        if (Boolean.TRUE.equals(coupon.getIsActive()))
        {
            if (coupon.getExpiryAt().isBefore(LocalDateTime.now()))
            {
                throw new BadRequestException("Expired coupon cannot be modified");
            }

            coupon.setIsActive(false);

            return "Coupon Disabled Successfully";
        }

        // If coupon is currently DISABLED → enable it
        else
        {
            if (coupon.getExpiryAt().isBefore(LocalDateTime.now()))
            {
                throw new BadRequestException("Coupon is already expired");
            }

            if (coupon.getValidFrom().isAfter(LocalDateTime.now()))
            {
                throw new BadRequestException("Coupon is not valid yet");
            }

            coupon.setIsActive(true);

            return "Coupon Enabled Successfully";
        }
    }

    public ProductPageResponseDTO<CouponResponseDTOForUser> viewAllActiveCoupon(Integer pageNumber, Integer pageSize)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        CouponFilterRequestAdmin filterRequestUser = new CouponFilterRequestAdmin();
        filterRequestUser.setStatus(CouponStatus.ACTIVE);

        Sort sort = Sort.by(Sort.Order.asc("expiryAt"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Specification<Coupon> spec =  couponSpecification.buildSpecification(filterRequestUser);
        Page<Coupon> coupons = couponRepository.findAll(spec,pageable);



        List<CouponResponseDTOForUser> dto = coupons.getContent().stream().map
                (coupon ->
                {
                    CouponResponseDTOForUser responseDTOForUser = mapCouponToDTOForUser(coupon);
                    long usage = orderRepository.countByUserIdAndCouponId(user.getId(),coupon.getId());
                    responseDTOForUser.setAlreadyUsed(usage>=coupon.getPerUserLimit());
                    return responseDTOForUser;
                }).toList();

        ProductPageResponseDTO<CouponResponseDTOForUser> response = new ProductPageResponseDTO<>();
        response.setContent(dto);
        response.setTotalPages(coupons.getTotalPages());
        response.setTotalElements(coupons.getTotalElements());
        response.setPageSize(coupons.getSize());
        response.setCurrentPage(coupons.getNumber());
        response.setLast(coupons.isLast());
        return response;
    }

    public CouponResponseDTOForUser viewActiveCoupon(Long id)
    {
        CouponFilterRequestAdmin filterRequestUser = new CouponFilterRequestAdmin();
        filterRequestUser.setStatus(CouponStatus.ACTIVE);
        Specification<Coupon> spec = couponSpecification.buildSpecification(filterRequestUser).
                and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("id"),id));
        Coupon coupon = couponRepository.findOne(spec)
                .orElseThrow(()-> new BadRequestException("Coupon is not Active Anymore!!!"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        CouponResponseDTOForUser responseDTOForUser = mapCouponToDTOForUser(coupon);
        long usage = orderRepository.countByUserIdAndCouponId(user.getId(), coupon.getId());
        if(usage>=coupon.getPerUserLimit())
        {
            responseDTOForUser.setAlreadyUsed(true);
        }
        return responseDTOForUser;
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
            throw new BadRequestException("Coupon usage limit exceeded,Please Remove it");
        }

        long usage = orderRepository.countByUserIdAndCouponId(user.getId(), coupon.getId());
        if(usage>=coupon.getPerUserLimit())
        {
            throw new BadRequestException("Coupon Already used!!!");
        }

        if(cart.getCoupon()!=null &&
                !cart.getCoupon().getCouponCode().equals(code))
        {
            throw new BadRequestException("Another coupon already applied on cart");
        }

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

        if (coupon.getCouponType() == CouponType.FLAT) {

            discount = coupon.getDiscountAmount();

        } else if (coupon.getCouponType() == CouponType.PERCENTAGE) {

            discount = totalAmount
                    .multiply(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);

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
