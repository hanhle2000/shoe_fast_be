package org.graduate.shoefastbe.dto.product;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDtoRequest {
    private Integer page;
    private Integer count;
    private List<Long> categoryIds;
    private List<Long> brandIds;
    private Double min;
    private Double max;
}
