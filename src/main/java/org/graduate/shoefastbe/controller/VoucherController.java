package org.graduate.shoefastbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.voucher.VoucherDtoResponse;
import org.graduate.shoefastbe.entity.Voucher;
import org.graduate.shoefastbe.service.voucher.VoucherService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voucher")
@CrossOrigin
@AllArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/by-code")
    @Operation(summary = "Lấy voucher theo code")
    VoucherDtoResponse getVoucherByCode(@RequestParam String code){
        return voucherService.getVoucherByCode(code);
    }

    @GetMapping("/list")
    Page<VoucherDtoResponse> getAllVoucher(@ParameterObject Pageable pageable){
        return voucherService.getAllVoucher(pageable);
    }
    @PostMapping("/create")
    VoucherDtoResponse create(@RequestBody Voucher voucher){
        return voucherService.create(voucher);
    }
    @GetMapping("/detail")
    VoucherDtoResponse getDetail(@RequestParam Long id){
        return voucherService.getDetail(id);
    }
    @PostMapping("/update")
    VoucherDtoResponse update(@RequestBody Voucher voucher){
        return voucherService.update(voucher);
    }
}
