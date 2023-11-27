package com.timeco.application.Repository;

import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findByIsListed(boolean blocked);

    public List<Category> findByNameContaining(String searchTerm);


    Category findByNameIgnoreCase(String categoryName);



    List<Category> findByCategoryOfferCategoryOfferId(Integer offerId);
}
