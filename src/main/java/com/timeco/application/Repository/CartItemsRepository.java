package com.timeco.application.Repository;

import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems,Long> {
    CartItems findByProductId(Long productId);


    Optional<CartItems> findByCartAndProduct(Cart cart, Product product);

    List<CartItems> findByCart(Cart cart);




    void deleteItemsBycartItemId(Long itemId);

    CartItems findByCartItemId(Long itemId);

    List<CartItems> findCartItemsByCart(Cart cart);



    Boolean findProductIsPresentByProduct(Product product);


//    Product findProductByProduct(Product product);

    CartItems findByProduct(Product product);
}
