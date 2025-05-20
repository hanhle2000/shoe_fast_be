package org.graduate.shoefastbe.service.cartitem;

import org.graduate.shoefastbe.base.error_success_handle.SuccessResponse;
import org.graduate.shoefastbe.dto.cart.CartItemDetailResponse;
import org.graduate.shoefastbe.dto.cart.CartItemDtoRequest;
import org.graduate.shoefastbe.dto.cart.CartItemDtoResponse;

import java.util.List;

public interface CartItemService {

    CartItemDtoResponse modifyCartItem(CartItemDtoRequest cartItemDtoRequest);
    Boolean isEnoughStock(Long id, Long quantity);
    List<CartItemDetailResponse> getCartItemDetailByAccount(Long id);
    SuccessResponse removeCartItem(CartItemDtoRequest cartItemDtoRequest);
}
