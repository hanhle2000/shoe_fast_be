package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    List<Attribute> findAllByProductIdAndSize(Long productId, Long size);
    List<Attribute> findAllByProductId(Long productId);
    List<Attribute> findAllByProductIdIn(Collection<Long> productIds);
    List<Attribute> findAllByIdIn(Collection<Long> ids);
    Attribute findByProductIdAndSize(Long productId, Long size);
}
