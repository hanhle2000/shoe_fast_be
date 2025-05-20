package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByProductId(Long productId);
    List<Image> findAllByProductIdIn(Collection<Long> productIds);
}
