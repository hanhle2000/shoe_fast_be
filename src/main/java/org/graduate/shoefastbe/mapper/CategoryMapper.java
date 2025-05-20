package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.category.CategoryRequest;
import org.graduate.shoefastbe.dto.category.CategoryResponse;
import org.graduate.shoefastbe.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface CategoryMapper {
    CategoryResponse getResponseBy(Category category);
    Category getEntityBy(CategoryRequest categoryRequest);
    void update(@MappingTarget Category category, CategoryRequest categoryRequest);

}
