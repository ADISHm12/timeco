package com.timeco.application.Dto;

import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.user.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CouponDto {

    private String couponName;

    private String couponCode;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private double couponAmount;

    private String minimumAmount;

    private String userLimit;

    public CouponDto() {
        super();
    }


    public CouponDto(String couponName, String couponCode, String description, LocalDate expireDate, double couponAmount, String minimumAmount, String userLimit) {
        this.couponName = couponName;
        this.couponCode = couponCode;
        this.description = description;
        this.expireDate = expireDate;
        this.couponAmount = couponAmount;
        this.minimumAmount = minimumAmount;
        this.userLimit = userLimit;
    }

    public String getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(String userLimit) {
        this.userLimit = userLimit;
    }



    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }


}