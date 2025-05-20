package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByCategoryIdIn(Collection<Long> ids);
    List<ProductCategory> findAllByProductId(Long productId);
    ProductCategory findByProductIdAndCategoryId(Long productId, Long categoryId);
}
