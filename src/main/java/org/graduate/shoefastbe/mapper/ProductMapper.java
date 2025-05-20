package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.product.CreateProductRequest;
import org.graduate.shoefastbe.dto.product.ProductDtoResponse;
import org.graduate.shoefastbe.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {
    ProductDtoResponse getResponseFromEntity(Product product);
    void update (@MappingTarget Product product, CreateProductRequest createProductRequest);
}
