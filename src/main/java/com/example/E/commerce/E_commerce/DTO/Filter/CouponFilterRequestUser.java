package com.example.E.commerce.E_commerce.DTO.Filter;

import com.example.E.commerce.E_commerce.Entity.Coupon.CouponStatus;
import com.example.E.commerce.E_commerce.Entity.Coupon.CouponType;
import lombok.Data;

@Data
public class CouponFilterRequestUser
{
    private CouponStatus couponStatus;
    private CouponType couponType;
}
