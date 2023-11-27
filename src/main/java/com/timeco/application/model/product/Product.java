package com.timeco.application.model.product;


import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_name",nullable = false)
    private String productName;

    @Transient
    @Column(name="current_state",nullable = false)
    private String current_state;

    @Column(name="description",nullable = false)
    private String description;

    @Column(name="quantity",nullable = false)
    private Integer quantity;

    @Column(name="price",nullable = false)
    private Double price;


    private boolean isBlocked;


    private Double discountedAmount;




    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    @ManyToOne(fetch =FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subcategory_id", referencedColumnName = "subcategory_Id")
    private Subcategory subcategory; // The subcategory to which this product belongs


    public void addProductImage(ProductImage productImage) {
        productImage.setProduct(this);
        productImages.add(productImage);
    }

    public void removeProductImage(ProductImage productImage) {
        productImages.remove(productImage);
        productImage.setProduct(null);
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public Product() {
        super();
        this.productImages = new ArrayList<>();
    }

    public Product(Long id, String productName,boolean isBlocked, String current_state, String description, Integer quantity, Double price, List<ProductImage> productImages, Category category,Double discountedAmount) {
        this.id = id;
        this.productName = productName;
        this.current_state = current_state;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.productImages = productImages;
        this.category = category;
        this.isBlocked=isBlocked;
        this.discountedAmount=discountedAmount;
    }

    public Double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(Double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrent_state() {
        // Set the current_state based on the quantity
        if (quantity > 0) {
            return "Stock In";
        } else {
            return "Stock Out";
        }

    }

    public void setCurrent_state(String current_state) {
        this.current_state = current_state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
