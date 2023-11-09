package com.timeco.application.web.admincontrollers;

import com.timeco.application.Dto.CouponDto;
import com.timeco.application.Repository.CouponRepository;
import com.timeco.application.Service.coupon.CouponService;
import com.timeco.application.model.coupon.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCouponController {

    @Autowired
    CouponService couponService ;

    @Autowired
    CouponRepository couponRepository ;

    @GetMapping("/couponList")
    public String listCoupon(Model model){
        List<Coupon> coupons = couponRepository.findAll();
        model.addAttribute("coupons", coupons);

        return "coupon-list";
    }

    @GetMapping("/addCoupon")
    public String showAddCouponPage(Model model){

        CouponDto coupon = new CouponDto();
        model.addAttribute("coupon",coupon);

        return "AddCoupon";

    }

    @PostMapping("/addCoupon")
    public String saveCoupon(@ModelAttribute CouponDto couponDto ){

        couponService.save(couponDto);
        return "redirect:/admin/couponList";
    }

    @GetMapping("/blockCoupon/{id}")
    public String blockCoupon(@PathVariable Integer id){
        couponService.lockCoupon(id);

        return "redirect:/admin/couponList";
    }
    @GetMapping("/unblockCoupon/{id}")
    public String unblockCoupon(@PathVariable Integer id){
        couponService.unlockCoupon(id);

        return "redirect:/admin/couponList";
    }

    @GetMapping("/editCoupon/{id}")
    public String editCoupon(@PathVariable Integer id, Model model){

        Coupon coupon = couponRepository.findById(id).orElse(null);

        model.addAttribute("coupon",coupon);

        return "edit-coupon";
    }

    @PostMapping("/editCoupon/{id}")
    public String updateCoupon(@PathVariable Integer id,@ModelAttribute Coupon coupon ){
        couponService.updateCoupon(id,coupon);
        return "redirect:/admin/couponList";
    }
}
