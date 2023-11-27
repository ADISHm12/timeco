package com.timeco.application.Dto;

import com.timeco.application.model.category.Category;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class CategoryOfferDto {

    private String categoryOfferCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate  startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private boolean isActive;
    private Category category;

    private List<Category> categories;


    private Integer percentage;

    private Long categoryId;




    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }


    public CategoryOfferDto(String categoryOfferCode, LocalDate startDate, LocalDate endDate, boolean isActive, Category category, Integer percentage, Long categoryId) {
        this.categoryOfferCode = categoryOfferCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.category = category;
        this.percentage = percentage;
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CategoryOfferDto() {
    }


}
