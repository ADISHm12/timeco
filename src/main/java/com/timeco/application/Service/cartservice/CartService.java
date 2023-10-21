package com.timeco.application.Service.cartservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.user.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Service
public interface CartService {

    public void addProductToCart(ProductDto productDTO, Principal principal);

    public List <CartItems> getCartItemsForUser(User  user);


}
