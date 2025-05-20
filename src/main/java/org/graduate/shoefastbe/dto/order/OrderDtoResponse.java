package org.graduate.shoefastbe.dto.order;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDtoResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private Double total;
    private String note;
    private String payment;
    private String shipment;
    private String code;
    private String description;
    private Date shipDate;
    private LocalDate createDate;
    private LocalDate modifyDate;
    private Boolean isPending;
    private String encodeUrl;
    private Boolean seen;
    private Long accountId;
    private Long voucherId;
    private Long discount;
    private Long orderStatusId;
    private String orderStatusName;
}
