package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.cart.CartItemDtoRequest;
import org.graduate.shoefastbe.dto.cart.CartItemDtoResponse;
import org.graduate.shoefastbe.entity.CartItem;
import org.mapstruct.Mapper;

@Mapper
public interface CartItemMapper {
    CartItem getEntityByRequest(CartItemDtoRequest cartItemDtoRequest);
    CartItemDtoResponse getResponseFrom(CartItem cartItem);
}
