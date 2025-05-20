package org.graduate.shoefastbe.dto.order;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class YearSynthesis {
    private Long year;
    private Long count;
    private Double total;
}
