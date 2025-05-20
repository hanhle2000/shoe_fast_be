package org.graduate.shoefastbe.dto.cart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemDetailResponse {
    private Long id;
    private String image;
    private String name;
    private Long size;
    private Double price;
    private Long quantity;
    private Long stock;
    private Long discount;
    private Double lastPrice;
}
