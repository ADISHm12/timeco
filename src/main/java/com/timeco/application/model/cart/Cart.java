package com.timeco.application.model.cart;


import com.timeco.application.model.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL)
    private List<CartItems> cartItems;



    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    public Cart(Long cartId, User user, List<CartItems> cartItems) {
        super();
        this.cartId = cartId;
        this.user = user;
        this.cartItems = cartItems;
    }

    public Cart() {
        super();
        this.cartItems = new ArrayList<>();
    }
}
