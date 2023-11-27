package com.timeco.application.Repository;

import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {
    List<PurchaseOrder> findByUserId(Long id);

    List<PurchaseOrder> getPurchaseOrdersByUser(User user);

    List<PurchaseOrder> findByPaymentStatus(String success);


    @Query("SELECT po FROM PurchaseOrder po WHERE YEARWEEK(po.orderedDate) = YEARWEEK(NOW())")
    List<PurchaseOrder> findOrdersInCurrentWeek();

    @Query("SELECT po FROM PurchaseOrder po WHERE MONTH(po.orderedDate) = MONTH(NOW()) AND YEAR(po.orderedDate) = YEAR(NOW())")
    List<PurchaseOrder> findOrdersInCurrentMonth();


    @Query("SELECT po FROM PurchaseOrder po WHERE YEAR(po.orderedDate) = YEAR(NOW())")
    List<PurchaseOrder> findOrdersInCurrentYear();


    @Query("SELECT po FROM PurchaseOrder po WHERE DATE(po.orderedDate) = CURRENT_DATE()")
    List<PurchaseOrder> findOrdersForToday();

}
