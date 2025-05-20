package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderId(Long orderId);
    List<OrderDetail> findAllByOrderIdIn(Collection<Long> orderIds);
}
