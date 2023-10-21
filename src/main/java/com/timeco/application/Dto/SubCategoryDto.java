package com.timeco.application.Dto;

public class SubCategoryDto {
    private String name;

    private boolean isListed;


    public boolean isListed() {
        return isListed;
    }

    public void setListed(boolean listed) {
        isListed = listed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubCategoryDto(String name, boolean isListed) {
        this.name = name;
        this.isListed = isListed;
    }

    public SubCategoryDto() {
        super();
    }
}
