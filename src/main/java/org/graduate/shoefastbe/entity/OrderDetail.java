package org.graduate.shoefastbe.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double originPrice;
    private Long quantity;
    private Double sellPrice;
    private Long attributeId;
    private Long orderId;
}
