package com.timeco.application.Service.OrderService;

import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.model.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class pdfService {


    @Autowired
    private OrderItemRepository orderItemRepository;


    List <OrderItem> listAll(){
        List <OrderItem> orderItem = orderItemRepository.findByOrderStatus("delivered");

        Collections.reverse(orderItem);
        return  orderItem;

    }
}
