package org.graduate.shoefastbe.service.cartitem;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.base.error_success_handle.SuccessHandle;
import org.graduate.shoefastbe.base.error_success_handle.SuccessResponse;
import org.graduate.shoefastbe.common.Common;
import org.graduate.shoefastbe.common.enums.TypeImage;
import org.graduate.shoefastbe.dto.cart.CartItemDetailResponse;
import org.graduate.shoefastbe.dto.cart.CartItemDtoRequest;
import org.graduate.shoefastbe.dto.cart.CartItemDtoResponse;
import org.graduate.shoefastbe.entity.*;
import org.graduate.shoefastbe.mapper.CartItemMapper;
import org.graduate.shoefastbe.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CartItemServiceImpl implements CartItemService{
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final AttributeRepository attributeRepository;
    private final AccountRepository accountRepository;
    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    @Override
    @Transactional
    public CartItemDtoResponse modifyCartItem(CartItemDtoRequest cartItemDtoRequest) {
        Attribute attribute = attributeRepository.findById(cartItemDtoRequest.getAttributeId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        Account account = accountRepository.findById(cartItemDtoRequest.getAccountId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        CartItem cartItem = cartItemRepository.findCartItemByAccountIdAndAttributeId(cartItemDtoRequest.getAccountId(),
                cartItemDtoRequest.getAttributeId());
        if(Objects.isNull(cartItem)){
            if(cartItemDtoRequest.getQuantity() > attribute.getStock()){
                throw new RuntimeException(CodeAndMessage.ERR5);
            }else{
                CartItem cartItemEntity = CartItem.builder()
                        .accountId(account.getId())
                        .attributeId(attribute.getId())
                        .quantity(cartItemDtoRequest.getQuantity())
                        .lastPrice(cartItemDtoRequest.getLastPrice())
                        .isActive(Boolean.TRUE)
                        .build();
                 cartItemRepository.save(cartItemEntity);
                 return cartItemMapper.getResponseFrom(cartItemEntity);
            }
        }else{
            long flag = cartItemDtoRequest.getQuantity();
            if(flag == 0){
                cartItem.setQuantity(flag);
                cartItem.setIsActive(Boolean.FALSE);
                cartItemRepository.save(cartItem);
                return cartItemMapper.getResponseFrom(cartItem);
            }else if(flag > attribute.getStock()){
                throw new RuntimeException(CodeAndMessage.ERR5);
            }else{
                cartItem.setQuantity(flag);
                cartItem.setIsActive(Boolean.TRUE);
                cartItemRepository.save(cartItem);
                return cartItemMapper.getResponseFrom(cartItem);
            }
        }
    }

    @Override
    public Boolean isEnoughStock(Long id, Long quantity) {
        Attribute attribute = attributeRepository.findById(id).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if(attribute.getStock() < quantity){
            throw new RuntimeException(CodeAndMessage.ERR5);
        }
        return true;
    }

    @Override
    public List<CartItemDetailResponse> getCartItemDetailByAccount(Long id) {
        List<CartItem> cartItemEntities = cartItemRepository.findByAccountIdAndIsActive(id,Boolean.TRUE);
        Map<Long, Attribute> attributeMap = attributeRepository.findAllByIdIn(cartItemEntities
                .stream()
                .map(CartItem::getAttributeId)
                .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(
                        Attribute::getId, Function.identity()
                ));

        Set<Long> productIds = attributeMap.values().stream().map(Attribute::getProductId).collect(Collectors.toSet());
        List<Product> productEntities = productRepository.findAllByIdIn(productIds);
        Map<Long, Long> productSaleMap = productEntities.stream().collect(Collectors.toMap(
                Product::getId, Product::getSaleId
        ));
        Map<Long, String> imageMap = imageRepository.findAllByProductIdIn(productIds).stream().filter(
                image -> image.getName().equals(TypeImage.main.name())
        ).collect(Collectors.toMap(Image::getProductId, Image::getImageLink));

        Map<Long, Sales> salesEntityMap = salesRepository.findAllByIdIn(productEntities
                .stream()
                .map(Product::getSaleId)
                .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(
                        Sales::getId,Function.identity()
                ));

        return cartItemEntities.stream().map(
                cartItem -> {
                    Attribute attribute = attributeMap.get(cartItem.getAttributeId());
                    return CartItemDetailResponse
                            .builder()
                            .id(attribute.getId())
                            .size(attribute.getSize())
                            .stock(attribute.getStock())
                            .price(attribute.getPrice())
                            .name(attribute.getName())
                            .image(imageMap.get(attribute.getProductId()))
                            .discount(salesEntityMap.get(productSaleMap.get(attribute.getProductId())).getDiscount())
                            .quantity(cartItem.getQuantity())
                            .lastPrice(cartItem.getLastPrice())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SuccessResponse removeCartItem(CartItemDtoRequest cartItemDtoRequest) {
        CartItem cartItem = cartItemRepository.findCartItemByAccountIdAndAttributeId(cartItemDtoRequest.getAccountId(),
                cartItemDtoRequest.getAttributeId());
        if(cartItem == null){
            throw new RuntimeException(CodeAndMessage.ERR3);
        }
        cartItem.setQuantity(0L);
        cartItem.setIsActive(Boolean.FALSE);
        cartItemRepository.save(cartItem);
        return SuccessHandle.success(CodeAndMessage.ME100);
    }
}
