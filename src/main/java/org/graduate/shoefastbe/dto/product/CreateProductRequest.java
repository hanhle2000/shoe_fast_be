package org.graduate.shoefastbe.dto.product;

import lombok.*;
import org.graduate.shoefastbe.dto.category.AttributeDtoRequest;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateProductRequest {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long brandId;
    private Long saleId;
    private List<Long> categoryId;
    private List<String> imageUrl;
    private List<AttributeDtoRequest> attribute;
    private Boolean isActive;
}
