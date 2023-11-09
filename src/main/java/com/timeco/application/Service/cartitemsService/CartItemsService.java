package com.timeco.application.Service.cartitemsService;

import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartItemsService {

    public void updateQuantity(Long productId, Integer newQuantity);

    int updateQuantity(Integer count, Long productId, String username);

    double totalPrice(String username);

    public void removeFromCart(Long itemId);


    List<CartItems> findCartItems(User user);

    public void updateProductQuantity(Long productId, Integer newQuantity);
}
