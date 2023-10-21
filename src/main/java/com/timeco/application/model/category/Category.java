package com.timeco.application.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_Id")
    private Long id;
    @Column(name = "category_name",nullable = false)
    private String name;
    @Column(name = "isListed")
    public boolean isListed;



    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isListed() {
        return isListed;
    }

    public void setListed(boolean listed) {
        isListed = listed;
    }

    public Category(Long id, String name, boolean isListed) {
        super();
        this.id = id;
        this.name = name;
        this.isListed = isListed;
    }

    public Category() {
        super();
    }


}
