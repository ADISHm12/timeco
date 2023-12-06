package com.timeco.application.Repository;

import com.timeco.application.model.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Integer> {
    Coupon findByCouponCode(String couponCode);

    int countByIsActive(boolean b);
}
