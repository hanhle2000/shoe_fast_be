package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.dto.account.AccountResponse;
import org.graduate.shoefastbe.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(String username);
    Account findByUsernameAndIsActive(String username, Boolean isActive);
    Account findByIdAndIsActive(Long id, Boolean isActive);

    @Query("SELECT new org.graduate.shoefastbe.dto.account.AccountResponse( a.id, a.username, a.createDate, a.modifyDate, a.isActive , a.role," +
            " ad.fullName, ad.gender, ad.phone, ad.email, ad.address, ad.birthdate ) FROM Account a " +
            "inner join AccountDetail ad on a.id = ad.accountId where a.role = ?1")
    List<AccountResponse> findAccountByRoleName(String name, Pageable pageable);

    @Query("SELECT distinct new org.graduate.shoefastbe.dto.account.AccountResponse( a.id, a.username, a.createDate, a.modifyDate, a.isActive , a.role," +
            " ad.fullName, ad.gender, ad.phone, ad.email, ad.address, ad.birthdate ) FROM Account a " +
            "inner join AccountDetail ad on a.id = ad.accountId ")
    List<AccountResponse> findAllAccount(Pageable pageable);
}
