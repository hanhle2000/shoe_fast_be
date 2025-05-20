package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.brands.BrandRequest;
import org.graduate.shoefastbe.dto.brands.BrandResponse;
import org.graduate.shoefastbe.entity.Brands;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface BrandsMapper {
    BrandResponse getResponseBy(Brands brands);
    Brands getEntityBy(BrandRequest brandRequest);
    void update(@MappingTarget Brands brands, BrandRequest brandRequest);
}
