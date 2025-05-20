package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.ProductAccountLikeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAccountLikeMapRepository extends JpaRepository<ProductAccountLikeMap, Long> {
    ProductAccountLikeMap findByProductIdAndAccountId(Long productId, Long accountId);
    List<ProductAccountLikeMap> findAllByAccountIdAndLiked(Long accountId, Boolean liked);
    List<ProductAccountLikeMap> findAllByAccountId(Long accountId);
}
