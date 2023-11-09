package com.timeco.application.model.order;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "PaymentMethod")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long paymentMethodId;

    @Column(name ="PaymentMethodName",unique=true,nullable=true)
    private String PaymentMethodName;


    @OneToMany(mappedBy="paymentMethod")
    private List<PurchaseOrder> orders = new ArrayList<>();


    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentMethodName() {
        return PaymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        PaymentMethodName = paymentMethodName;
    }

    public List<PurchaseOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<PurchaseOrder> orders) {
        this.orders = orders;
    }

    public PaymentMethod(String paymentMethodName, List<PurchaseOrder> orders) {
        super();
        PaymentMethodName = paymentMethodName;
        this.orders = orders;
    }

    public PaymentMethod() {
        super();
    }
}
