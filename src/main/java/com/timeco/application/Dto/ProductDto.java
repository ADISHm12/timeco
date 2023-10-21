package com.timeco.application.Dto;

import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;
import com.timeco.application.model.product.ProductImage;

import java.util.List;

public class ProductDto {

    private Long id;
    private String productName;
    private String current_state;
    private String description;
    private Integer quantity;
    private Double price;
    private Long categoryId;
    private Long subcategoryId;
    private List<ProductImage> imageNames;

    // Add a field to hold the Category object
    private Category category;
    private Subcategory subcategory;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrent_state() {
        return current_state;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<ProductImage> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<ProductImage> imageNames) {
        this.imageNames = imageNames;
    }

    // Getter and Setter for Category
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductDto() {
        super();
    }

    public ProductDto(String productName, String current_state, String description, Integer quantity, Double price, Long categoryId) {
        this.productName = productName;
        this.current_state = current_state;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.categoryId = categoryId;

    }

    public ProductDto(String productName, String current_state, String description, Integer quantity, Double price, Long categoryId, Long subcategoryId, List<ProductImage> imageNames, Category category, Subcategory subcategory) {
        this.productName = productName;
        this.current_state = current_state;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.imageNames = imageNames;
        this.category = category;
        this.subcategory = subcategory;
    }
}
