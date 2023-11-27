package com.timeco.application.Service.OrderService;

import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.PurchaseOrderRepository;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository ;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository ;

    @Autowired
    private ProductRepository productRepository ;

    public List <OrderItem > convertCartItemsToOrderItems(List<CartItems> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();


        for (CartItems cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemCount(cartItem.getQuantity());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrderStatus("Order Placed");
            orderItem.setOrderItemCount(cartItem.getQuantity());
            double price =0.0;
            if(cartItem.getProduct().getDiscountedAmount()!=null){
                price = cartItem.getProduct().getDiscountedAmount();

            }else {
                price = cartItem.getProduct().getPrice();
            }
            orderItem.setOrderedPrice(price);
            orderItems.add(orderItem);

        }

        return orderItems;
    }
    public Long calculateTotalRevenueForDeliveredItems() {
        List<OrderItem> deliveredOrderItems = orderItemRepository.findByOrderStatus("delivered");

        Long totalRevenue = (long) deliveredOrderItems.stream()
                .mapToDouble(orderItem -> (orderItem.getOrderItemCount() * orderItem.getProduct().getPrice()))
                .sum();

        Long totalCost = (long) deliveredOrderItems.stream()
                .mapToDouble(orderItem -> (0.6 * orderItem.getOrderItemCount() * orderItem.getProduct().getPrice()))
                .sum();

        Long totalProfit = totalRevenue - totalCost;

        return totalProfit;

    }

    public int countCod() {
        List<OrderItem> allOrderItems = orderItemRepository.findAll(); // Get all OrderItems
        int count = 0;

        for (OrderItem orderItem : allOrderItems) {
            if (orderItem.getOrder() != null &&
                    orderItem.getOrder().getPaymentMethod() != null &&
                    "CASH ON DELIVERY".equals(orderItem.getOrder().getPaymentMethod().getPaymentMethodName())) {
                count++;
            }

        }
        return  count;
    }
    public int countOnline() {
        List<OrderItem> allOrderItems = orderItemRepository.findAll(); // Get all OrderItems
        int count = 0;

        for (OrderItem orderItem : allOrderItems) {
            if (orderItem.getOrder() != null &&
                    orderItem.getOrder().getPaymentMethod() != null &&
                    "ONLINE PAYMENT".equals(orderItem.getOrder().getPaymentMethod().getPaymentMethodName())) {
                count++;
            }

        }
        return  count;
    }

    public long countOrderItems() {
       long orderItems = orderItemRepository.count();

        return orderItems;
    }


    public Long totalRevenue(String period) {
        List<PurchaseOrder > purchaseOrdersList = new ArrayList<>();
        switch (period) {
            case "week=" -> {
                purchaseOrdersList = purchaseOrderRepository.findOrdersInCurrentWeek();

            }
            case "month=" -> {
                purchaseOrdersList = purchaseOrderRepository.findOrdersInCurrentMonth();
            }
            case "day=" -> {
                purchaseOrdersList = purchaseOrderRepository.findOrdersForToday();
            }
            default -> {
                purchaseOrdersList = purchaseOrderRepository.findOrdersInCurrentYear();
            }
        }
        double totalCost = 0;
        for(PurchaseOrder purchaseOrder : purchaseOrdersList)
        {
            for(OrderItem orderItem : purchaseOrder.getOrderItems()) {
                if(orderItem.getOrderStatus().equals("delivered")) {
                    System.out.println(orderItem.getOrderItemId());
                    long cost = (long) ((orderItem.getOrderItemCount() * orderItem.getProduct().getPrice()) * .6);
                    totalCost += cost;
                }
            }

        }
        System.out.println(totalCost);
        return (long) totalCost;

    }


    public Integer totalSales(String period) {
        List<OrderItem> orderItemList = new ArrayList<>();

        switch (period) {
            case "week=":
                orderItemList = orderItemRepository.findOrderItemsInCurrentWeek();
                break;
            case "month=":
                orderItemList = orderItemRepository.findOrderItemsInCurrentMonth();
                break;
            case "day=":
                orderItemList = orderItemRepository.findOrderItemsForToday();
                break;
            default:
                orderItemList = orderItemRepository.findOrderItemsInCurrentYear();
                break;
        }

        int totalSalesCount  = 0;

        for (OrderItem orderItem : orderItemList) {

                if (orderItem.getOrderStatus().equals("delivered")) {
                    System.out.println(orderItem.getOrderItemId() );
                    totalSalesCount++;

                }
            }


        System.out.println(totalSalesCount);
        return totalSalesCount;
    }

//    public Map <String, Integer> countOrderItemsByMonth() {
//        Map<String, Integer> orderItemCountByMonth = new HashMap<>();
//        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
//
//        for (PurchaseOrder order : orders) {
//            String orderedDate = order.getOrderedDate().toString();
//            String month = orderedDate.substring(5, 7); // Extract the month part from the date string
//
//            // If the month is not in the map, initialize it with 1; otherwise, increment the count.
//            orderItemCountByMonth.put(month, orderItemCountByMonth.getOrDefault(month, 0) + order.getOrderItems().size());
//        }
//
//        return orderItemCountByMonth;
//    }
public Map<String, Integer> countOrderItemsByMonth() {
    Map<String, Integer> orderItemCountByMonth = new HashMap<>();

    // Initialize the map with zero values for all 12 months
    for (int month = 1; month <= 12; month++) {
        orderItemCountByMonth.put(String.format("%02d", month), 0);
    }

    List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

    for (PurchaseOrder order : orders) {
        String orderedDate = order.getOrderedDate().toString();
        String month = orderedDate.substring(5, 7); // Extract the month part from the date string

        // Increment the count for the corresponding month
        orderItemCountByMonth.put(month, orderItemCountByMonth.get(month) + order.getOrderItems().size());
    }

    return orderItemCountByMonth;
}

    public int getOnlinePaymentRevenue() {
        List<OrderItem> onlineDeliveredOrders = orderItemRepository.findWithOrderStatusAndPaymentMethod("delivered", "ONLINE PAYMENT");

        int totalRevenue = 0;

        for (OrderItem orderItem : onlineDeliveredOrders) {
            totalRevenue += orderItem.getProduct().getPrice();
        }

        return totalRevenue;
    }

    public int getCODPaymentRevenue() {
        List<OrderItem> onlineDeliveredOrders = orderItemRepository.findWithOrderStatusAndPaymentMethod("delivered", "CASH ON DELIVERY");

        int totalRevenue = 0;

        for (OrderItem orderItem : onlineDeliveredOrders) {
            totalRevenue += orderItem.getProduct().getPrice();
        }

        return totalRevenue;
    }

    public List<OrderItem> findOrderItemsByCategory(Category  category) {
         List<OrderItem> orderItems = new ArrayList<>();

        // Retrieve all products belonging to the provided category
        List<Product > productsInCategory = productRepository.findByCategoryId(category.getId());

        // For each product in the category, find its associated order items
        for (Product product : productsInCategory) {
            List<OrderItem> orderItemsForProduct = orderItemRepository.findByProduct(product);
            orderItems.addAll(orderItemsForProduct);
        }

        return orderItems;
    }

    public Page<OrderItem> findAllOrderItem(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);

        return orderItemRepository.findAll(pageable);
    }

    public int countWallet() {
        List<OrderItem> allOrderItems = orderItemRepository.findAll(); // Get all OrderItems
        int count = 0;

        for (OrderItem orderItem : allOrderItems) {
            if (orderItem.getOrder() != null &&
                    orderItem.getOrder().getPaymentMethod() != null &&
                    "WALLET".equals(orderItem.getOrder().getPaymentMethod().getPaymentMethodName())) {
                count++;
            }

        }
        return  count;
    }

    public int getWalletPaymentRevenue() {
        List<OrderItem> onlineDeliveredOrders = orderItemRepository.findWithOrderStatusAndPaymentMethod("delivered", "WALLET");

        int totalRevenue = 0;

        for (OrderItem orderItem : onlineDeliveredOrders) {
            totalRevenue += orderItem.getProduct().getPrice();
        }

        return totalRevenue;
    }



    public int cancelOrder() {
        List<OrderItem> cancelOrder = orderItemRepository.findByOrderStatus("cancelled");
        int count = 0;
        return count = cancelOrder.size();
    }
}
