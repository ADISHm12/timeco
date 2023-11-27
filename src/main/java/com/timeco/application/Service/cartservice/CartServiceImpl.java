package com.timeco.application.Service.cartservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CartRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.product.Product;
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
            System.out.println(username+"jh66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666");
            User user = userService.findUserByUsername(username); // Assuming you have a repository method to find the user by username
            System.out.println(user+"3333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
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

    @Override
    public boolean isProductInCart(ProductDto productDTO, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        Cart usercart = cartRepository.findCartByUser(user);

        for (CartItems  cartItem : usercart.getCartItems()) {
            // Assuming that cart items have a reference to the product
            if (cartItem.getProduct().getId().equals(productDTO.getId())) {

                return true; // Product is in the cart
            }
        }

        return false;
    }

    @Override
    public void addToCartFromWishlist(Cart cart, Product product) {
        CartItems cartItem = new CartItems() ;
        cartItem.setProduct(product);
        cartItem.setQuantity(1); // You can set the quantity as needed
        cartItem.setPrice(product.getPrice()); // Set the price based on the product's price
        cartItem.setCart(cart); // Associate the cart item with the cart

        // Add the cart item to the cart's list of items
        cart.getCartItems().add(cartItem);

        // Save the updated cart to the database
        cartRepository.save(cart);
    }


}
