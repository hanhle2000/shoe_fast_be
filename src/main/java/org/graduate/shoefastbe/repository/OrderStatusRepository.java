package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    OrderStatus findByName(String name);
    List<OrderStatus> findAllByIdIn(Collection<Long> ids);
}
