package com.timeco.application.model.category;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CategoryOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryOfferId;

    @Column(name = "category_Offer_Code", nullable = false)
    private String  categoryOfferCode;

    @Column(name = "start_Date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;



    @Column(name = "end_Date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private LocalDate endDate;

    private Integer percentage;
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    @OneToMany(mappedBy = "categoryOffer")
    private List<Category> categories = new ArrayList<>() ;


    public CategoryOffer(String categoryOfferCode, LocalDate startDate, LocalDate endDate, Integer percentage, boolean isActive, List<Category> categories) {
        this.categoryOfferCode = categoryOfferCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.isActive = isActive;
        this.categories = categories;
    }

    public CategoryOffer() {
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getCategoryOfferId() {
        return categoryOfferId;
    }

    public void setCategoryOfferId(Integer categoryOfferId) {
        this.categoryOfferId = categoryOfferId;
    }

    public String getCategoryOfferCode() {
        return categoryOfferCode;
    }

    public void setCategoryOfferCode(String categoryOfferCode) {
        this.categoryOfferCode = categoryOfferCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive(boolean b) {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
