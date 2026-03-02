package com.example.E.commerce.E_commerce.Controller.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.ApplyCouponResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.CouponResponseUserDTO;
import com.example.E.commerce.E_commerce.DTO.Coupon.getAllCouponResponseDTO;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/coupon")
@PreAuthorize("hasRole('USER')")
public class CouponUserController
{
    private final CouponService couponService;
    @GetMapping
    public Page<getAllCouponResponseDTO> getAllActiveCoupon(@RequestParam (defaultValue = "0") Integer pageNumber,
                                                            @RequestParam (defaultValue = "10") Integer pageSize)
    {
        return couponService.viewAllActiveCoupon(pageNumber,pageSize);
    }

    @GetMapping("/{id}")
    public getAllCouponResponseDTO getActiveCouponById(@PathVariable Long id)
    {
        return couponService.viewActiveCoupon(id);
    }
    @PostMapping("/apply")
    public CouponResponseUserDTO applyCoupon(@RequestBody ApplyCouponResponseDTO dto)
    {
        return couponService.applyCoupon(dto);
    }
    @DeleteMapping("/remove/{id}")
    public String deleteAppliedCoupon(@PathVariable Long id)
    {
        return couponService.removeCoupon(id);
    }
}
