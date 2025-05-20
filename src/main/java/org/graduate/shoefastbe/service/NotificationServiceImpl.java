package org.graduate.shoefastbe.service;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.notification.NotificationResponse;
import org.graduate.shoefastbe.entity.Notification;
import org.graduate.shoefastbe.entity.Order;
import org.graduate.shoefastbe.entity.Product;
import org.graduate.shoefastbe.repository.NotificationRepository;
import org.graduate.shoefastbe.repository.OrderRepository;
import org.graduate.shoefastbe.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    @Override
    public List<NotificationResponse> loadNotification(Boolean read, Boolean deliver) {
        List<Notification> notifications = notificationRepository.getNotificationByReadEqualsAndDeliverEquals(read, deliver);
        List<Long> productIds = notifications.stream().map(Notification::getProductId).collect(Collectors.toList());
        List<Long> orderIds = notifications.stream().map(Notification::getOrderId).collect(Collectors.toList());
        Map<Long,Product> productMap = productRepository.findAllByIdIn(productIds).stream().collect(Collectors.toMap(
                Product::getId, Function.identity()
        ));
        Map<Long, Order> orderMap = orderRepository.findAllByIdIn(orderIds).stream().collect(Collectors.toMap(
                Order::getId, Function.identity()
        ));
        return notifications.stream().map(
                notification -> NotificationResponse.builder()
                            .id(notification.getId())
                            .type(notification.getType())
                            .order(orderMap.getOrDefault(notification.getOrderId(),new Order()))
                            .product(productMap.getOrDefault(notification.getProductId(),new Product()))
                            .content(notification.getContent())
                            .deliver(notification.getDeliver())
                            .read(notification.getRead())
                            .build()
        ) .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse modifyNotification(Long id) {
        Notification notification = notificationRepository.findById(id).get();
        notification.setRead(true);
        notification.setDeliver(true);
        notificationRepository.save(notification);
        Map<Long,Product> productMap = productRepository.findAllByIdIn(Collections.singleton(notification.getProductId())).stream().collect(Collectors.toMap(
                Product::getId, Function.identity()
        ));
        Map<Long, Order> orderMap = orderRepository.findAllByIdIn(Collections.singleton(notification.getOrderId())).stream().collect(Collectors.toMap(
                Order::getId, Function.identity()
        ));
        return  NotificationResponse.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .order(orderMap.getOrDefault(notification.getOrderId(),new Order()))
                        .product(productMap.getOrDefault(notification.getProductId(),new Product()))
                        .content(notification.getContent())
                        .deliver(notification.getDeliver())
                        .read(notification.getRead())
                        .build();
    }

    @Override
    @Transactional
    public NotificationResponse updateNotification(Notification notification) {
        notificationRepository.save(notification);
        Map<Long,Product> productMap = productRepository.findAllByIdIn(Collections.singleton(notification.getProductId())).stream().collect(Collectors.toMap(
                Product::getId, Function.identity()
        ));
        Map<Long, Order> orderMap = orderRepository.findAllByIdIn(Collections.singleton(notification.getOrderId())).stream().collect(Collectors.toMap(
                Order::getId, Function.identity()
        ));
        return  NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .order(orderMap.getOrDefault(notification.getOrderId(),new Order()))
                .product(productMap.getOrDefault(notification.getProductId(),new Product()))
                .content(notification.getContent())
                .deliver(notification.getDeliver())
                .read(notification.getRead())
                .build();
    }
}
