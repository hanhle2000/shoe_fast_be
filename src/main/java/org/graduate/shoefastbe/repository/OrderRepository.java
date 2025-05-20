package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.dto.order.MonthSynthesis;
import org.graduate.shoefastbe.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByAccountId(Long accountId, Pageable pageable);
    Page<Order> findAllByAccountIdAndOrderStatusId(Long accountId, Long orderStatusId, Pageable pageable);
    Page<Order> findAllByOrderByCreateDateDesc(Pageable pageable);
    Page<Order> findAllByPayment(String payment,Pageable pageable);
    Page<Order> findAllByOrderStatusId(Long orderStatusId, Pageable pageable);
    List<Order> findAllByOrderStatusId(Long orderStatusId);
    List<Order> findAllByIdIn(Collection<Long> ids);
    Page<Order> findAllByOrderStatusIdAndPayment(Long orderStatusId,String payment, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE year(o.createDate) = :year and month(o.createDate) = :month")
    Page<Order> findOrderByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.orderStatusId = :id and year(o.createDate) = :year and month(o.createDate) = :month")
    Page<Order> findOrderByOrderStatusAndYearAndMonth(@Param("id") Long id, @Param("year") Integer year, @Param("month") Integer month, Pageable pageable);

    @Query("SELECT o FROM Order o inner join OrderDetail d on o.id = d.orderId inner join Attribute a on a.id = d.attributeId inner join Product p on p.id = a.productId where p.id = :id and o.orderStatusId = 4")
    Page<Order> findOrderByProduct(@Param("id") Long id, Pageable pageable);
    @Query("SELECT new org.graduate.shoefastbe.dto.order.MonthSynthesis(MONTH(o.createDate), COUNT(o.id), SUM (o.total)) FROM Order o WHERE o.orderStatusId = 4 AND YEAR(o.createDate) = :year GROUP BY MONTH(o.createDate) ORDER BY SUM (o.total) DESC")
    List<MonthSynthesis> reportAmountMonth(@Param("year") Integer year);
    @Query("SELECT o FROM Order o WHERE o.createDate between :from and :to")
    Page<Order> findOrderBetweenDate(@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.orderStatusId = :id and o.createDate between :from and :to")
    Page<Order> findOrderByOrderStatusBetweenDate(@Param("id") Long id, @Param("from")LocalDate from, @Param("to") LocalDate to, Pageable pageable);
}
