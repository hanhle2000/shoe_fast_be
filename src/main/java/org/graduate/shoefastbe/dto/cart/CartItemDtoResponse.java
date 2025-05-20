package org.graduate.shoefastbe.dto.cart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemDtoResponse {
    private Long id;
    private Long quantity;
    private Long accountId;
    private Long attributeId;
    private Double lastPrice;
    private Boolean isActive;
}