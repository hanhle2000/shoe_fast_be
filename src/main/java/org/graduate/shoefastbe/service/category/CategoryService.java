package org.graduate.shoefastbe.service.category;

import org.graduate.shoefastbe.dto.category.CategoryRequest;
import org.graduate.shoefastbe.dto.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> getAllCategory(Pageable pageable);
    Page<CategoryResponse> getAllCategoryAdmin(Pageable pageable);
    CategoryResponse create(CategoryRequest categoryRequest);
    CategoryResponse update(CategoryRequest categoryRequest);

    CategoryResponse getDetail(Long id);
}
