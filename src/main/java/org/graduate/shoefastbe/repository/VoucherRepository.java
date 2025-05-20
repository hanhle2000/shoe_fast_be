package org.graduate.shoefastbe.repository;

import org.graduate.shoefastbe.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Voucher findVoucherByCode(String code);
}
