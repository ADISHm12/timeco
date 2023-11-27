package com.timeco.application.Repository;

import com.timeco.application.model.category.Category;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem ,Long> {


    OrderItem findByOrderItemId(Long orderItemId);
    List<OrderItem> findOrderItemByOrder(PurchaseOrder Order);

//    Long countOrderItemByOrderStatus(String delivered);

    List<OrderItem> findByOrderStatus(String delivered);


    long count();

    @Query("SELECT oi FROM OrderItem oi WHERE YEARWEEK(oi.order.orderedDate) = YEARWEEK(NOW()) AND oi.orderStatus = 'delivered'")
    List<OrderItem> findDeliveredOrderItemsInCurrentWeek();

    @Query("SELECT oi FROM OrderItem oi WHERE YEAR(oi.order.orderedDate) = YEAR(NOW()) AND oi.orderStatus = 'delivered'")
    List<OrderItem> findDeliveredOrderItemsInCurrentYear();

    @Query("SELECT oi FROM OrderItem oi WHERE MONTH(oi.order.orderedDate) = MONTH(NOW()) AND YEAR(oi.order.orderedDate) = YEAR(NOW()) AND oi.orderStatus = 'delivered'")
    List<OrderItem> findDeliveredOrderItemsInCurrentMonth();

    @Query("SELECT oi FROM OrderItem oi WHERE DATE(oi.order.orderedDate) = CURRENT_DATE() AND oi.orderStatus = 'delivered'")
    List<OrderItem> findDeliveredOrderItemsInCurrentDay();

    @Query ("SELECT oi FROM OrderItem oi WHERE DATE(oi.order.orderedDate) = CURRENT_DATE")
    List<OrderItem> findOrderItemsForToday();

    @Query("SELECT oi FROM OrderItem oi WHERE YEAR(oi.order.orderedDate) = YEAR(NOW())")
    List<OrderItem> findOrderItemsInCurrentYear();


    @Query("SELECT oi FROM OrderItem oi WHERE MONTH(oi.order.orderedDate) = MONTH(NOW()) AND YEAR(oi.order.orderedDate) = YEAR(NOW())")
    List<OrderItem> findOrderItemsInCurrentMonth();

    @Query("SELECT oi  FROM OrderItem oi WHERE YEARWEEK(oi.order.orderedDate) = YEARWEEK(NOW())")
    List<OrderItem> findOrderItemsInCurrentWeek();

    List<OrderItem> findByOrderStatusAndOrderOrderedDateBetween(String orderStatus, LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM OrderItem o WHERE o.orderStatus = :orderStatus AND o.order.paymentMethod.PaymentMethodName = :paymentMethodName")
    List<OrderItem> findWithOrderStatusAndPaymentMethod(String orderStatus, String paymentMethodName);


    List<OrderItem> findByProduct(Product product);
}
