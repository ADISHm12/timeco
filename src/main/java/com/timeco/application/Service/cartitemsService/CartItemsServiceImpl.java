package com.timeco.application.Service.cartitemsService;

import com.timeco.application.Repository.CartItemsRepository;
import com.timeco.application.Repository.CartRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemsServiceImpl implements CartItemsService{

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository ;
    @Autowired
    private ProductService productService;


    @Override
    public void updateQuantity(Long productId, Integer newQuantity) {

    }

    @Override
    @Transactional
    public int updateQuantity(Integer count, Long productId, String username) {
        User user=userRepository.findByEmail(username);

        Product product = productService.getProductById(productId);
        System.out.println(product);

        Cart  cart =cartRepository.findCartByUser(user);
        System.out.println(cart);
        Optional<CartItems> cartItems = cartItemsRepository.findByCartAndProduct(cart, product);
        System.out.println(cartItems);
        if(cartItems.isPresent()){
            int updateQuantity=cartItems.get().getQuantity()+count;
            updateQuantity=Math.max(updateQuantity,1);
            cartItems.get().setQuantity(updateQuantity);


            cartItemsRepository.save(cartItems.get());

            return  updateQuantity;
        }
        return 1;
    }

    @Override
    public double totalPrice(String username) {
        User user = userRepository.findByEmail(username);
        Cart cart = cartRepository.findCartByUser(user);

        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);
        int totalPrice = 0;

        for(CartItems cartItem : cartItems){
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            Double productPrice = product.getPrice();

            if (product.getCategory() != null && product.getCategory().getCategoryOffer() != null && !product.getCategory().getCategoryOffer().isActive()) {
                System.out.println("checkingggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
                productPrice = product.getDiscountedAmount();
            } else {
                productPrice = product.getPrice();
            }

            Double subtotal = quantity * productPrice;
            totalPrice += subtotal;



        }
            return totalPrice;
    }

    @Override
    @Transactional
    public void removeFromCart(Long itemId) {
            cartItemsRepository.deleteItemsBycartItemId(itemId);

    }

    @Override
    public List<CartItems> findCartItems(User user) {

        Cart cart = cartRepository.findCartByUser(user);
        if (cart != null) {
            List<CartItems> cartItems = cartItemsRepository.findCartItemsByCart(cart);

            return cartItems;
        } else {
            return null;
        }    }

    @Override
    @Transactional
    public void updateProductQuantity(Long productId, Integer newQuantity) {
        CartItems  cartItem = cartItemsRepository.findByProductId(productId);


        if (cartItem != null) {

            int currentQuantity = cartItem.getQuantity();
            int updatedQuantity = currentQuantity + newQuantity;
            cartItem.setQuantity(updatedQuantity);

            System.out.println(cartItem.getQuantity());

            cartItemsRepository.save(cartItem);
        }
    }

    @Override
    public void deleteCartItems(List<CartItems> cartItems) {
        cartItemsRepository.deleteAll(cartItems);
    }
}
