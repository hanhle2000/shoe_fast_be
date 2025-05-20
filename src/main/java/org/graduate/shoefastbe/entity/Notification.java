package org.graduate.shoefastbe.entity;

import lombok.*;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Boolean read;
    private Boolean deliver;
    private Long orderId;
    private Long productId;
    private Long type; // 1- đơn hàng dc tạo, 2- đơn hàng đã hủy, 3- sp sắp hết số lượng
}
