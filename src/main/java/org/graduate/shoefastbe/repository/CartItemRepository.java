package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findCartItemByAccountIdAndAttributeId(Long accountId, Long attributeId);
    List<CartItem> findByAccountIdAndIsActive(Long accountId, Boolean isActive);

}
