package com.timeco.application.model.coupon;

import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.user.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;

    private String couponName;
    private String couponCode;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private double couponAmount;

    private String minimumAmount;
    private String userLimit;

    private Integer usageCount=0;

    private boolean isActive;

    @OneToMany (mappedBy="coupon")
    private List <PurchaseOrder> ordersList;

    @ManyToMany(mappedBy = "coupons")
    private Set<User> users = new HashSet<>();

    public Coupon() {

    }


    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
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





    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<PurchaseOrder> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<PurchaseOrder> ordersList) {
        this.ordersList = ordersList;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }


    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }


    public String getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(String userLimit) {
        this.userLimit = userLimit;
    }



    public Coupon(String couponName, String couponCode, String description, LocalDate expireDate, double couponAmount, String minimumAmount, String userLimit, Integer usageCount, boolean isActive, List<PurchaseOrder> ordersList, Set<User> users) {
        this.couponName = couponName;
        this.couponCode = couponCode;
        this.description = description;
        this.expireDate = expireDate;
        this.couponAmount = couponAmount;
        this.minimumAmount = minimumAmount;
        this.userLimit = userLimit;
        this.usageCount = usageCount;
        this.isActive = isActive;
        this.ordersList = ordersList;
        this.users = users;
    }
}
