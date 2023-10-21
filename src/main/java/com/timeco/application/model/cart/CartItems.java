package com.timeco.application.model.cart;

import com.timeco.application.model.product.Product;

import javax.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItem_id")
    private long cartItemId;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")//some time we use big decimal
    private double price;


    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CartItems(long cartItemId, Cart cart, Product product, Integer quantity, double price) {

        super();
        this.cartItemId = cartItemId;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public CartItems() {
        super();
    }
}
