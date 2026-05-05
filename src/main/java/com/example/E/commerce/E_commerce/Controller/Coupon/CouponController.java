//package com.example.E.commerce.E_commerce.Controller.Coupon;
//import com.example.E.commerce.E_commerce.DTO.Coupon.*;
//import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
//import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/user/coupons")
//@PreAuthorize("hasRole('USER')")
//public class CouponController
//{
//    private final CouponService couponService;
//
//    @GetMapping("/active/all")
//    public ProductPageResponseDTO<CouponResponseDTOForUser> getAllActiveCoupon(@RequestParam(defaultValue = "0") Integer pageNumber ,
//                                                                               @RequestParam(defaultValue = "10") Integer pageSize){
//        return couponService.viewAllActiveCoupon(pageNumber,pageSize);
//    }
//
//    @GetMapping("/active/view/{id}")
//    public CouponResponseDTOForUser getActiveCoupon(@PathVariable Long id)
//    {
//        return couponService.viewActiveCoupon(id);
//    }
//
//    @DeleteMapping("/applied/delete/id")
//    public String DeleteAppliedCoupon(@PathVariable Long id, Authentication authentication)
//    {
//        return couponService.removeCoupon("id");
//    }
//    @PostMapping("/apply")
//    public CouponResponseUserDTO addCoupon(@RequestBody ApplyCouponResponseDTO dto)
//    {
//        return couponService.applyCoupon(dto);
//    }
//}

package com.example.E.commerce.E_commerce.Controller.Coupon;

import com.example.E.commerce.E_commerce.DTO.Coupon.*;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.Service.Coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/coupons")
@PreAuthorize("hasRole('USER')")
public class CouponController
{
    private final CouponService couponService;

    @GetMapping("/active/all")
    public ProductPageResponseDTO<CouponResponseDTOForUser> getAllActiveCoupon(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        return couponService.viewAllActiveCoupon(pageNumber, pageSize);
    }

    @GetMapping("/active/view/{id}")
    public CouponResponseDTOForUser getActiveCoupon(@PathVariable Long id)
    {
        return couponService.viewActiveCoupon(id);
    }

    @PostMapping("/apply")
    public CouponResponseUserDTO applyCoupon(@RequestBody ApplyCouponResponseDTO dto)
    {
        return couponService.applyCoupon(dto);
    }

    @DeleteMapping("/applied/remove")
    public String removeAppliedCoupon()
    {
        return couponService.removeCoupon(null);
    }
}