package com.timeco.application.Service.coupon;

import com.timeco.application.Dto.CouponDto;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.user.User;
import org.springframework.stereotype.Service;

@Service
public interface CouponService {


     void lockCoupon(Integer id);


     void unlockCoupon(Integer id) ;


    void save(CouponDto couponDto);

    void updateCoupon(Integer id, Coupon coupon);

    boolean isExpired(Coupon coupon);

    boolean hasUsedCoupon(User user, Coupon coupon);

   void setAvailableCouponsCount() ;

}
