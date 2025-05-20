package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.order.OrderDtoRequest;
import org.graduate.shoefastbe.dto.order.OrderDtoResponse;
import org.graduate.shoefastbe.entity.Order;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {
    Order getOrderByRequest(OrderDtoRequest orderDtoRequest);
    OrderDtoResponse getResponseByEntity(Order order);
}
