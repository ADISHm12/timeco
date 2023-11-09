package com.timeco.application.model.order;

import com.timeco.application.model.product.Product;

import javax.persistence.*;

@Entity
@Table(name = "OrderItem")
public class OrderItem {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long orderItemId;

    private Integer orderItemCount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder order;


    public OrderItem() {
        super();
    }

    private String orderStatus;

    public OrderItem(Integer orderItemCount, Product product, PurchaseOrder order, String orderStatus) {
        super();
        this.orderItemCount = orderItemCount;
        this.product = product;
        this.order = order;
        this.orderStatus = orderStatus;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getOrderItemCount() {
        return orderItemCount;
    }

    public void setOrderItemCount(Integer orderItemCount) {
        this.orderItemCount = orderItemCount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PurchaseOrder getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }



    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
