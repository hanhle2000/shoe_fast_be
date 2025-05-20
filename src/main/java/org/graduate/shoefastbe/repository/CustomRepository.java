package org.graduate.shoefastbe.repository;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.base.filter.Filter;
import org.graduate.shoefastbe.entity.Attribute;
import org.graduate.shoefastbe.entity.ProductCategory;
import org.graduate.shoefastbe.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final EntityManager entityManager;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    public Page<Product> getProductRelate(Long productId, Long brandId, Pageable pageable) {
        return Filter.builder(Product.class, entityManager)
                .filter()
                .isEqual("brandId", brandId)
                .isNotIn("id", Collections.singleton(productId))
                .getPage(pageable);
    }

    public Page<Product> getProductBySearch(String search, Pageable pageable) {
        return Filter.builder(Product.class, entityManager)
                .filter()
                .isContain("name", search)
                .getPage(pageable);
    }
    public List<Attribute> getAttributeByProductId(Collection<Long> productIds) {
         List<Attribute> attributeEntities = Filter.builder(Attribute.class, entityManager)
                .filter()
                .isEqual("size", 39L)
                .isIn("productId", productIds)
                 .getPage(PageRequest.of(0, 9999999)).getContent();
        if (attributeEntities.isEmpty()) {
            throw new RuntimeException(CodeAndMessage.ERR3);
        }
        return attributeEntities;
    }
    public Page<Attribute> getAttributeFilter(Collection<Long> brandIds, Collection<Long> categoryIds,
                                              Double min, Double max, Pageable pageable) {
        List<Long> productBrandIds = productRepository.findAllByBrandIdIn(brandIds).stream().map(Product::getId)
                .collect(Collectors.toList());
        List<Long> productCategoryIds = productCategoryRepository.findAllByCategoryIdIn(categoryIds).stream()
                .map(ProductCategory::getProductId)
                .collect(Collectors.toList());

        Page<Attribute> attributeEntities = Filter.builder(Attribute.class, entityManager)
                .filter()
                .isEqual("size", 39L)
                .isIn("productId", productCategoryIds)
                .isIn("productId", productBrandIds)
                .isGreaterThanOrEqual("price", min)
                .isLessThanOrEqual("price", max)
                .getPage(pageable);
        if (attributeEntities.isEmpty()) {
           return Page.empty();
        }
        return attributeEntities;
    }

}
