package com.timeco.application.Repository;

import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByProductNameContaining(String searchTerm, Pageable pageable);
    List<Product>findByProductNameContaining(String searchTerm);
    Optional<Product> findById(Long id);

    List<Product> findByCategory(Category category);

    List<Product> findAllByIdNot(Long id);


    boolean existsByProductNameIgnoreCase(String productName);

    List<Product> findByCategoryId(Long id);

    Page<Product> findAllByIsBlockedFalse(Pageable pageable);


    Page<Product> findByCategoryAndPriceBetween(Category category, double minPrice, double maxPrice, Pageable pageable);

    Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    List<Product> findTop3ByOrderByIdDesc();
}
