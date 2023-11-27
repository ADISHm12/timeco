package com.timeco.application.model.returnReason;

import com.timeco.application.model.order.PurchaseOrder;

import javax.persistence.*;

@Entity
public class ReturnReason {

    @Id
    @GeneratedValue (strategy= GenerationType .IDENTITY)
    private Integer returReasonId;

    private String returnReason;

    private String comment;

    @OneToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder  order;

    public Integer getReturReasonId() {
        return returReasonId;
    }

    public void setReturReasonId(Integer returReasonId) {
        this.returReasonId = returReasonId;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PurchaseOrder getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }

    public ReturnReason() {
        super();
    }

}
