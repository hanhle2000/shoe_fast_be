package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.attribute.AttributeDtoResponse;
import org.graduate.shoefastbe.entity.Attribute;
import org.mapstruct.Mapper;

@Mapper
public interface AttributeMapper {
    AttributeDtoResponse getResponseFromEntity(Attribute attribute);
}
