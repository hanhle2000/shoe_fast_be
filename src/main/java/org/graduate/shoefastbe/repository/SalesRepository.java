package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    List<Sales> findAllByIdIn(Collection<Long> ids);
}
