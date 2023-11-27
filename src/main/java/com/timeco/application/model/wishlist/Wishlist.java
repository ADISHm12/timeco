package com.timeco.application.model.wishlist;


import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue (strategy= GenerationType.IDENTITY)
    private Integer wishlistId;

    @OneToOne
    @JoinColumn (name = "user_id")
    private User user;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "wishlist_product",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set <Product > products = new HashSet<>();


    public Wishlist(User user, Set<Product> products) {
        super();
        this.user = user;
        this.products = products;
    }

    public Wishlist() {
        super();
    }

    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
