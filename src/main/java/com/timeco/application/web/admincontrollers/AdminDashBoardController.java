package com.timeco.application.web.admincontrollers;


import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Repository.PurchaseOrderRepository;
import com.timeco.application.Service.OrderService.OrderItemService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminDashBoardController {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService ;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserService userService ;



    @GetMapping("/adminHome")
    public String home(Model model ) {

        String year = "year";

        // Calculate revenue based on the selected period
        Long revenue = orderItemService.totalRevenue(year);

        // Fetch other statistics and data as you did before
        int sales = orderItemService.totalSales("delivered");
        int stock = productService.getTotalStock();
        List<OrderItem> orderItems = orderItemRepository.findAll();
        Collections.reverse(orderItems);
        model.addAttribute("orderItems", orderItems);
        List<OrderItem> first10Orders = orderItems.subList(0, Math.min(15, orderItems.size()));
        int count = userService.countCustomers();
        int COD_count = orderItemService.countCod();
        int ONLINE_count = orderItemService.countOnline();
        long totalOrders = orderItemService.countOrderItems();
        int totalOnlinePayment = orderItemService.getOnlinePaymentRevenue();
        int totalCashOnDelivery = orderItemService.getCODPaymentRevenue();

        model.addAttribute("Orders", totalOrders);
        model.addAttribute("customer", count);
        model.addAttribute("ONLINE", ONLINE_count);
        model.addAttribute("COD", COD_count);
        model.addAttribute("latestOrders", first10Orders);
        model.addAttribute("stock", stock);
        model.addAttribute("sales", sales);
        model.addAttribute("totalRevenue", revenue);
        model.addAttribute("totalOnlinePayment", totalOnlinePayment);
        model.addAttribute("totalCashOnDelivery", totalCashOnDelivery);

        return "adminHome";
    }
    @PostMapping("/adminHome")
    @ResponseBody
    public Map<String, Object> adminHome(@RequestBody String period)

    {
        System.out.println("controller called"  + period);
        Long revenue = orderItemService.totalRevenue(period);
        String status = "delivered";
       Integer totalSales = orderItemService.totalSales(period);
//        Integer totalStocks = dashboardService.totalStocks();
        Map<String, Object> response = new HashMap<>() ;
        response.put("sales", totalSales);
        response.put("totalRevenue", revenue);
//        response.put("stocks", totalStocks);
        return response;


    }


}
