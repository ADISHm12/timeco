package com.timeco.application.Service.cartitemsService;

import org.springframework.stereotype.Service;

@Service
public interface CartItemsService {

    public void updateQuantity(Long productId, Integer newQuantity);

    int updateQuantity(Integer count, Long productId, String username);

    double totalPrice(String username);
}
