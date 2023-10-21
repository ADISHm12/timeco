package com.timeco.application.Repository;

import com.timeco.application.model.category.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<Subcategory,Long> {


}
