package com.timeco.application.model.product;

import com.timeco.application.Dto.ProductDto;

import javax.persistence.*;

@Entity
@Table(name="product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name", nullable = false)
    private String imageName;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductImage(Long id, String imageName, Product product) {
        super();
        this.id = id;
        this.imageName = imageName;

        this.product = product;
    }

    public ProductImage() {
        super();
    }


}
