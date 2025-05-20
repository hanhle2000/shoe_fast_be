package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long> {
    Boolean existsByEmail(String email);
    AccountDetail findByAccountId(Long accountId);
    AccountDetail findByEmail(String email);
}
