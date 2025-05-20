package org.graduate.shoefastbe.service.attribute;

import org.graduate.shoefastbe.dto.attribute.AttributeDtoResponse;

public interface AttributeService {
    AttributeDtoResponse getAttributeByProductId(Long productId, Long size);
    AttributeDtoResponse getAttributeById(Long id);
}
