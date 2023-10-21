package com.timeco.application.model.product;

import net.bytebuddy.implementation.bind.annotation.Super;

import java.util.Base64;
import java.util.List;

public class ProductImageDto {


        private Long productId;
        private String productName;
        private String currentStatus;
        private String description;
        private Integer quantity;
        private Double price;
        private List<byte[]> imageDatas;

    public ProductImageDto(Long productId, String productName, String currentStatus, String description, Integer quantity, Double price, List<byte[]> imageDatas) {

       super();
        this.productId = productId;
        this.productName = productName;
        this.currentStatus = currentStatus;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.imageDatas = imageDatas;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
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

    public List<byte[]> getImageDatas() {
        return imageDatas;
    }

    public void setImageDatas(List<byte[]> imageDatas) {
        this.imageDatas = imageDatas;
    }

    public ProductImageDto() {
        super();
    }

    public void addBase64ImageData(String base64ImageData) {
        // Add the provided base64 image data to the list of imageDatas.
        // You can add it as a byte array, for example:
        byte[] imageData = Base64.getDecoder().decode(base64ImageData);
        imageDatas.add(imageData);
    }
}
