package org.graduate.shoefastbe.service.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.dto.voucher.VoucherDtoResponse;
import org.graduate.shoefastbe.entity.Voucher;
import org.graduate.shoefastbe.mapper.VoucherMapper;
import org.graduate.shoefastbe.repository.VoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@Service
@Transactional(readOnly = true)
public class VoucherServiceImpl implements VoucherService{
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    @Override
    public VoucherDtoResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findVoucherByCode(code);
        if(Objects.nonNull(voucher)){
            if(voucher.getExpireDate().isBefore(LocalDate.now())){
                throw new RuntimeException(CodeAndMessage.ERR6);
            }
            if(!voucher.getIsActive()){
                throw new RuntimeException(CodeAndMessage.ERR7);
            }
            if(voucher.getCount() == 0){
                throw new RuntimeException(CodeAndMessage.ERR8);
            }
            return voucherMapper.getResponseByEntity(voucher);
        }else{
            throw new RuntimeException(CodeAndMessage.ERR3);
        }
    }

    @Override
    public Page<VoucherDtoResponse> getAllVoucher(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.asc("id")));
        Page<Voucher> vouchers =  voucherRepository.findAll(sortedPageable);
        return vouchers.map(voucherMapper::getResponseByEntity);
    }

    @Override
    @Transactional
    public VoucherDtoResponse create(Voucher voucher) {
         Voucher voucher1 = Voucher.builder()
                 .code(voucher.getCode())
                 .count(voucher.getCount())
                 .isActive(Boolean.TRUE)
                 .discount(voucher.getDiscount())
                 .expireDate(voucher.getExpireDate())
                 .createDate(LocalDate.now())
                 .build();
         voucherRepository.save(voucher1);
        return voucherMapper.getResponseByEntity(voucher1);
    }

    @Override
    public VoucherDtoResponse getDetail(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        return voucherMapper.getResponseByEntity(voucher);
    }

    @Override
    @Transactional
    public VoucherDtoResponse update(Voucher voucher) {
        Voucher vou = voucherRepository.findById(voucher.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        vou.setCode(voucher.getCode());
        vou.setExpireDate(voucher.getExpireDate());
        vou.setCreateDate(LocalDate.now());
        vou.setDiscount(voucher.getDiscount());
        vou.setCount(voucher.getCount());
        vou.setIsActive(voucher.getIsActive());
        voucherRepository.save(vou);
        return voucherMapper.getResponseByEntity(vou);
    }

}
