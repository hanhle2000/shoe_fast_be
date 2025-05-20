package org.graduate.shoefastbe.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDtoResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long view;
    private Double price;
    private String image;
    private String brand;
    private Long discount;
    private Boolean isActive;
    private Boolean liked;
    private Double similarity;
}
