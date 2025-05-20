package org.graduate.shoefastbe.service.voucher;

import org.graduate.shoefastbe.dto.voucher.VoucherDtoResponse;
import org.graduate.shoefastbe.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

    VoucherDtoResponse getVoucherByCode(String code);
    Page<VoucherDtoResponse> getAllVoucher(Pageable pageable);
    VoucherDtoResponse create(Voucher voucher);
    VoucherDtoResponse getDetail(Long id);
    VoucherDtoResponse update(Voucher voucher);
}
