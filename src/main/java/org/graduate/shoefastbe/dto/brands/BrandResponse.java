package org.graduate.shoefastbe.dto.brands;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BrandResponse {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Boolean isActive;
    private LocalDate createDate;
    private LocalDate modifyDate;
}
