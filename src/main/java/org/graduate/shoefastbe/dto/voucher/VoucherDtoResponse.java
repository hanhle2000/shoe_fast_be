package org.graduate.shoefastbe.dto.voucher;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VoucherDtoResponse {
    private Long id;
    private String code;
    private Long count;
    private Long discount;
    private LocalDate createDate;
    private LocalDate expireDate;
    private Boolean isActive;
}
