package org.graduate.shoefastbe.dto.sale;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SaleResponse {
    private Long id;
    private String name;
    private String description;
    private Long discount;
    private Boolean isActive;
    private LocalDate modifyDate;
    private LocalDate createDate;
}
