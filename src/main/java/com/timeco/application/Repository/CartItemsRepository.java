package com.timeco.application.Repository;

import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems,Long> {
    CartItems findByProductId(Long productId);


    Optional<CartItems> findByCartAndProduct(Cart cart, Product product);

    List<CartItems> findByCart(Cart cart);
}
