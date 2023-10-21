package com.timeco.application.Repository;

import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Cart findCartByUser(User  user);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.id = :cartId")
    Cart findCartWithItemsEagerly(Long cartId);
}
