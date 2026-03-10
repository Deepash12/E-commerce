package com.example.E.commerce.E_commerce.Controller.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.AddCouponRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.getAllCouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequestAdmin;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponValidationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/coupon")
@PreAuthorize("hasRole('ADMIN')")
public class CouponAdminController
{
    private final CouponService couponService;
    private final CouponValidationService couponValidationService;

    public CouponAdminController(CouponService couponService, CouponValidationService couponValidationService) {
        this.couponService = couponService;
        this.couponValidationService = couponValidationService;
    }

    @PostMapping("/createCoupon")
    public CouponResponseDTO createCoupon(@RequestBody @Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        try {
            couponValidationService.validateForCreation(addCouponRequestDTO);
            return couponService.addCoupon(addCouponRequestDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/updateCoupon/{id}")
    public CouponResponseDTO updateCoupon
            (@PathVariable Long id,@RequestBody @Valid AddCouponRequestDTO addCouponRequestDTO)
    {
        return couponService.updateCoupon(id,addCouponRequestDTO);
    }

    @GetMapping("/all")
    public Page<getAllCouponResponseDTO> getAllCoupon(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                   @RequestParam(required = false) String couponCode,
                                   @RequestParam(required = false) CouponType couponType,
                                   @RequestParam(required = false) Boolean isActive,
                                   @RequestParam(required = false) CouponStatus status)
    {
        CouponFilterRequestAdmin filter = new CouponFilterRequestAdmin();
        filter.setCouponCode(couponCode);
        filter.setCouponType(couponType);
        filter.setIsActive(isActive);
        filter.setStatus(status);
        return couponService.viewAllCoupon(pageNumber,pageSize,filter);
    }
    @GetMapping("/view/{id}")
    public getAllCouponResponseDTO getCouponById(@PathVariable Long id)
    {
        return couponService.viewCoupon(id);
    }
    @PatchMapping("/toggle/{id}")
    public ResponseEntity<String> toggleCoupon(@PathVariable Long id)
    {
        return ResponseEntity.ok(couponService.toggleCouponStatus(id));
    }



}
