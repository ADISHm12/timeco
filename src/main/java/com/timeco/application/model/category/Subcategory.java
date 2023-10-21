package com.timeco.application.model.category;

import javax.persistence.*;

@Entity
@Table(name = "Subcategory")
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_Id")
    private Long id;

    @Column(name = "subcategory_name", nullable = false)
    private String name;

    @Column(name = "isListed")
    private boolean isListed;

    public boolean isListed() {
        return isListed;
    }

    public void setListed(boolean listed) {
        isListed = listed;
    }
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "category_id", referencedColumnName = "category_Id")
//    private Category category; // The parent category to which this subcategory belongs

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


    public Subcategory(Long id, String name, boolean isListed) {
        this.id = id;
        this.name = name;
        this.isListed = isListed;
    }

    public Subcategory() {
        super();
    }
}
