package org.graduate.shoefastbe.dto.cart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemDtoRequest {
    private Long accountId;
    private Long attributeId;
    private Long quantity;
    private Double lastPrice;
}
