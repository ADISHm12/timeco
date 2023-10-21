package com.timeco.application.Dto;

public class CategoryDto {


    private String name;

    private boolean isListed;

    public CategoryDto(String name,boolean isListed) {
        super();
        this.name = name;
        this.isListed=isListed;
    }

    public boolean isListed() {
        return isListed;
    }

    public void setListed(boolean listed) {
        isListed = listed;
    }

    public CategoryDto() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
