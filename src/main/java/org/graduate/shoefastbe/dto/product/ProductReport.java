package org.graduate.shoefastbe.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductReport {
    private Long id;
    private String name;
    private Double amount; // tong tien
    private Long quantity; // so luong san pham
    private Long count; // so luong don hang
}
