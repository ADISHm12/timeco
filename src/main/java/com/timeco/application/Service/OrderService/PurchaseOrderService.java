package com.timeco.application.Service.OrderService;



import com.timeco.application.model.order.OrderItem;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseOrderService {

    public double calculateOrderAmount(List<OrderItem> orderItems) {
        double totalAmount = 0.0;

        for (OrderItem orderItem : orderItems) {
            // Calculate the subtotal for each order item (item price * quantity) and add it to the total amount
            totalAmount += orderItem.getProduct().getPrice() * orderItem.getOrderItemCount();
        }

        return totalAmount;
    }

    public int calculateOrderedQuantity(List<OrderItem> orderItems) {
        int totalQuantity = 0;

        for (OrderItem orderItem : orderItems) {
            // Sum up the quantities of all order items
            totalQuantity += orderItem.getOrderItemCount();
        }

        return totalQuantity;
    }

}
