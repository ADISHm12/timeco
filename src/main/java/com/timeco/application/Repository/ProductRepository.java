package com.timeco.application.Repository;

import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByProductNameContaining(String searchTerm);

    Optional<Product> findById(Long id);

    List<Product> findByCategory(Category category);

    List<Product> findAllByIdNot(Long id);



}
