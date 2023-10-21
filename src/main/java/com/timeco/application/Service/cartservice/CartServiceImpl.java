package com.timeco.application.Service.cartservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CartRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;




    @Override
    public void addProductToCart(ProductDto productDTO, Principal principal) {

        if (principal != null) {
            String username = principal.getName(); // Get the username of the authenticated user
            User user = userService.findUserByUsername(username); // Assuming you have a repository method to find the user by username

            if (user != null) {
                Cart cart = user.getCart();

                if (cart == null) {
                    cart = new Cart();
                    cart.setUser(user);
                    user.setCart(cart);
                }

                CartItems cartItem = new CartItems();
                cartItem.setProduct(productService.getProductById(productDTO.getId()));
                cartItem.setQuantity(productDTO.getQuantity());
                cartItem.setCart(cart);
                cartItem.setPrice(cartItem.getProduct().getPrice() * productDTO.getQuantity());

                cart.getCartItems().add(cartItem);

                userRepository.save(user);
            }
        }
    }


@Override
@Transactional
public List<CartItems> getCartItemsForUser(User user) {
    Optional<User> userOptional = userRepository.findById(user.getId()); // Retrieve the user as an Optional


    if (userOptional.isPresent() && userOptional.get().getCart() != null) {
        return userOptional.get().getCart().getCartItems();
    } else {
        return Collections.emptyList();
    }
}

//    @Override
//    @Transactional
//    public List<CartItems> getCartItemsForUser(User user) {
//
//        System.out.println(user+"3333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
//        Cart cart = cartRepository.findCartWithItemsEagerly(user.getId());
//        System.out.println(cart+"22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
//
//        if (cart != null) {
//            return cart.getCartItems();
//        } else {
//            return Collections.emptyList();
//        }
//    }

}
