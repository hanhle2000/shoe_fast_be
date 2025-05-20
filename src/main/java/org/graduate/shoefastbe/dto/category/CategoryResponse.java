package org.graduate.shoefastbe.dto.category;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate createDate;
    private LocalDate modifyDate;
    private Boolean isActive;
}
