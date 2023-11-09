package com.timeco.application.web.admincontrollers;


import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Repository.PurchaseOrderRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository ;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository ;


    @GetMapping("/orderList")
    public String OrderList(Model  model){

        List<OrderItem> orderItems = orderItemRepository.findAll();
        Collections.reverse(orderItems);
        model.addAttribute("orderItems",orderItems);

        return "orderList";
    }
    @GetMapping("/orderEdit/{orderItemId}")
    public String orderEdit(@PathVariable Long orderItemId, Model model){

        OrderItem orderItem = orderItemRepository.findByOrderItemId(orderItemId);
        model.addAttribute("orderItem",orderItem);
        return"OrderEdit";
    }


    @GetMapping("/cancel-order/{orderItemId}")
    public String cancelOrder(@PathVariable Long orderItemId) {


        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);
        System.out.println(orderItemId+"sdjkhfgdsajkhg");
        if (orderItem != null) {
            orderItem.setOrderStatus("cancelled");
            orderItemRepository.save(orderItem);

        }
        return "redirect:/admin/orderEdit/" + orderItemId;
    }


    @PostMapping("/updateStatus/{orderItemId}")
    public String updateOrderStatus(@PathVariable Long orderItemId, @RequestParam("selectedStatus") String selectedStatus) {

        // Retrieve the order item by ID
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);

        if (orderItem != null) {
            // Update the order status
            orderItem.setOrderStatus(selectedStatus);
            orderItem.getOrder().setPaymentStatus("success");
            orderItemRepository.save(orderItem);
        }

        return "redirect:/admin/orderEdit/" + orderItemId;
    }
}
