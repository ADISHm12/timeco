package com.timeco.application.web.admincontrollers;


import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Repository.PurchaseOrderRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.OrderService.OrderItemService;
import com.timeco.application.model.order.OrderItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

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

    @Autowired
    private OrderItemService orderItemService ;




    @GetMapping("/orderList")
    public String orderList(Model model,@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "6") int pageSize){
        Page<OrderItem> orderItemsPage = orderItemService.findAllOrderItem(page,pageSize);

        model.addAttribute("orderItem", orderItemsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderItemsPage.getTotalPages());


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
