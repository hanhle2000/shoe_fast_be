package org.graduate.shoefastbe.dto.notification;

import lombok.*;
import org.graduate.shoefastbe.entity.Order;
import org.graduate.shoefastbe.entity.Product;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Boolean read;
    private Boolean deliver;
    private Order order;
    private Product product;
    private Long type;
}
