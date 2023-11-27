package com.timeco.application.Dto;

public class CategoryDto {


    private Long id;
    private String name;

    private boolean isListed;

    public CategoryDto(Long id, String name, boolean isListed) {
        this.id = id;
        this.name = name;
        this.isListed = isListed;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


}
