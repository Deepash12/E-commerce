package com.example.E.commerce.E_commerce.Service.Coupon;

import com.example.E.commerce.E_commerce.DTO.Filter.CouponFilterRequestAdmin;
import com.example.E.commerce.E_commerce.Entity.Coupon.Coupon;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Component
public class CouponSpecification
{
    public Specification<Coupon> buildSpecification(CouponFilterRequestAdmin filterRequest)
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if(filterRequest.getCouponCode()!=null)
            {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("couponCode")),
                        "%" + filterRequest.getCouponCode().toLowerCase()+"%"));
            }
            if(filterRequest.getCouponType()!=null)
            {
                predicates.add
                        (criteriaBuilder.equal
                                (root.get("couponType"),filterRequest.getCouponType())
                        );
            }
            if(filterRequest.getIsActive()!=null)
            {
                predicates.add
                        (
                                criteriaBuilder.equal
                                        (root.get("isActive"),filterRequest.getIsActive())
                        );
            }
            if(filterRequest.getStatus()!=null)
            {
                LocalDateTime now = LocalDateTime.now();
                switch (filterRequest.getStatus())
                {
                    case INACTIVE -> predicates.add(criteriaBuilder.isFalse(root.get("isActive")));
                    case NOT_YET_STARTED ->
                            predicates.add
                                    (criteriaBuilder.and
                                            (criteriaBuilder.isTrue
                                                    (root.get("isActive")
                                                    ), criteriaBuilder.greaterThan
                                                            (root.get("validFrom"),now)
                                            )
                                    );
                    case USAGE_LIMIT_REACHED -> predicates.add
                            (
                                    criteriaBuilder.greaterThanOrEqualTo
                                                            (root.get("usedCount"), root.get("globalUsageLimit"))

                            );
                    case EXPIRED -> predicates.add(criteriaBuilder.lessThan(root.get("expiryAt"),now));
                    case ACTIVE -> predicates.add
                            (
                                    criteriaBuilder.and
                                            (
                                                    criteriaBuilder.isTrue
                                                            (root.get("isActive")),criteriaBuilder.lessThanOrEqualTo
                                                            (
                                                                    root.get("validFrom"),now),
                                                    criteriaBuilder.greaterThanOrEqualTo(root.get("expiryAt"),now),
                                                    criteriaBuilder.lessThan(root.get("usedCount"),root.get("globalUsageLimit"))
                                            )
                            );

                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
