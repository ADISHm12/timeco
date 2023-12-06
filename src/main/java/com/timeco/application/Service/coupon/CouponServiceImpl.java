package com.timeco.application.Service.coupon;

import com.timeco.application.Dto.CouponDto;
import com.timeco.application.Repository.CouponRepository;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository ;

    @Override
    public void lockCoupon(Integer id) {
        Coupon lockCoupon = couponRepository.findById(id).get();
        lockCoupon.setActive(false);
        couponRepository.save(lockCoupon);
    }

    @Override
    public void unlockCoupon(Integer id) {

        Coupon unlockCoupon = couponRepository.findById(id).get();
        unlockCoupon.setActive(true);
        couponRepository.save(unlockCoupon);
    }

    @Override
    public void save(CouponDto couponDto) {
        Coupon coupon = new Coupon();

        coupon.setCouponName(couponDto.getCouponName());
        coupon.setCouponAmount(couponDto.getCouponAmount());
        coupon.setCouponCode(couponDto.getCouponCode());
        coupon.setDescription(couponDto.getDescription());
        coupon.setExpireDate(couponDto.getExpireDate());
        coupon.setMinimumAmount(couponDto.getMinimumAmount());
        coupon.setUserLimit(couponDto.getUserLimit());
        couponRepository.save(coupon);

        setAvailableCouponsCount();
    }

    @Override
    public void updateCoupon(Integer id, Coupon coupon) {
        Coupon updateCoupon = couponRepository.findById(id).orElse(null);
        if(updateCoupon != null){
            updateCoupon.setCouponAmount(coupon.getCouponAmount());
            updateCoupon.setCouponCode(coupon.getCouponCode());
            updateCoupon.setCouponName(coupon.getCouponName());
            updateCoupon.setUserLimit(coupon.getUserLimit());
            updateCoupon.setExpireDate(coupon.getExpireDate());
            updateCoupon.setUsageCount(coupon.getUsageCount());
            updateCoupon.setMinimumAmount(coupon.getMinimumAmount());
            updateCoupon.setDescription(coupon.getDescription());

            couponRepository.save(updateCoupon);
        }
    }
    @Override
    public boolean isExpired(Coupon coupon) {
        // Compare the current date with the coupon's expiration date
        return LocalDate.now().isAfter(coupon.getExpireDate());
    }

    @Override
    public boolean hasUsedCoupon(User user, Coupon coupon) {
        Set<Coupon> userCoupons = user.getCoupons();
        return userCoupons.contains(coupon);
    }

    @Override
    public void setAvailableCouponsCount() {
        int availableCouponsCount = couponRepository.countByIsActive(true);
        Coupon coupon = new Coupon();
        coupon.setAvailableCouponsCount(availableCouponsCount);
    }





}
