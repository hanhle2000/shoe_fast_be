package org.graduate.shoefastbe.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CancelOrderRequest {
    private Long id;
    private String description;
}
