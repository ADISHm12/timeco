package com.timeco.application.model.order;


import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;

import javax.mail.Address;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PurchaseOrder")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long orderId;

    @OneToOne
    private UserAddress address;

    @OneToMany(mappedBy="order",cascade=CascadeType.ALL)
    private List <OrderItem> orderItems= new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private PaymentMethod paymentMethod;

    private LocalDate orderedDate;

    private Double orderAmount;

    private String paymentStatus;

    private String transcationId;

    private Integer orderedQuantity;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public UserAddress getAddress() {
        return address;
    }

    public PurchaseOrder(UserAddress address, List<OrderItem> orderItems, User user, PaymentMethod paymentMethod, LocalDate orderedDate, Double orderAmount, String paymentStatus, String transcationId, Integer orderedQuantity) {
        this.address = address;
        this.orderItems = orderItems;
        this.user = user;
        this.paymentMethod = paymentMethod;
        this.orderedDate = orderedDate;
        this.orderAmount = orderAmount;
        this.paymentStatus = paymentStatus;
        this.transcationId = transcationId;
        this.orderedQuantity = orderedQuantity;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(LocalDate orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getTranscationId() {
        return transcationId;
    }

    public void setTranscationId(String transcationId) {
        this.transcationId = transcationId;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public PurchaseOrder(UserAddress address, List<OrderItem> orderItems, User user, PaymentMethod paymentMethod, LocalDate orderedDate, Double orderAmount, String transcationId, Integer orderedQuantity) {
        super();
        this.address = address;
        this.orderItems = orderItems;
        this.user = user;
        this.paymentMethod = paymentMethod;
        this.orderedDate = orderedDate;
        this.orderAmount = orderAmount;
        this.transcationId = transcationId;
        this.orderedQuantity = orderedQuantity;
    }

    public PurchaseOrder() {
        super();
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
