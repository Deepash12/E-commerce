package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.Coupon.AddCouponRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.getAllCouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequest;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponCalculationService;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
@PreAuthorize("hasRole('ADMIN')")
public class CouponAdminController
{
    private final CouponService couponService;
    private final CouponValidationService couponValidationService;
    private final CouponCalculationService couponCalculationService;


    @PostMapping("/createCoupon")
    private CouponResponseDTO createCoupon(@RequestBody @Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        couponValidationService.validateForCreation(addCouponRequestDTO);
        return couponService.addCoupon(addCouponRequestDTO);
    }
    @PutMapping("/updateCoupon/{id}")
    private CouponResponseDTO updateCoupon
            (@PathVariable Long id,@RequestBody @Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        return couponService.updateCoupon(id,addCouponRequestDTO);
    }
    @GetMapping
    public Page<getAllCouponResponseDTO> getAllCoupon(@PathVariable Integer pageNumber , @PathVariable Integer pageSize,@RequestBody CouponFilterRequest filter)
    {
        return couponService.viewAllCoupon(pageNumber,pageSize,filter);
    }


}
