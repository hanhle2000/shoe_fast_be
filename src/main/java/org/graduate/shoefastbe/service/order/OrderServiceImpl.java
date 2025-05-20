package org.graduate.shoefastbe.service.order;

import javax.persistence.OptimisticLockException;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.common.enums.OrderStatusEnum;
import org.graduate.shoefastbe.dto.order.*;
import org.graduate.shoefastbe.dto.product.ProductReport;
import org.graduate.shoefastbe.entity.*;
import org.graduate.shoefastbe.mapper.AttributeMapper;
import org.graduate.shoefastbe.mapper.OrderMapper;
import org.graduate.shoefastbe.repository.*;
import org.graduate.shoefastbe.util.MailUtil;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AccountRepository accountRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final VoucherRepository voucherRepository;
    private final AttributeRepository attributeRepository;
    private final NotificationRepository notificationRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AttributeMapper attributeMapper;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public OrderDtoResponse createOrder(OrderDtoRequest orderDtoRequest) {
        try {
            Account account = accountRepository.findById(orderDtoRequest.getAccountId()).orElseThrow(
                    () -> new RuntimeException(CodeAndMessage.ERR3)
            );
            OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.WAIT_ACCEPT.getValue());
            Order order = orderMapper.getOrderByRequest(orderDtoRequest);
            order.setOrderStatusId(orderStatus.getId());
            order.setSeen(Boolean.FALSE);
            order.setAccountId(account.getId());
            order.setCreateDate(LocalDate.now());
            order.setModifyDate(LocalDate.now());

            if (Objects.nonNull(orderDtoRequest.getCode()) && !orderDtoRequest.getCode().isEmpty()) {
                Voucher voucher = voucherRepository.findVoucherByCode(orderDtoRequest.getCode());
                if(voucher.getCount() <= 0) throw new RuntimeException(CodeAndMessage.ERR8);
                voucher.setCount(voucher.getCount() - 1);
                voucherRepository.save(voucher);
                order.setVoucherId(voucher.getId());
            }

            order.setEncodeUrl(null);
            orderRepository.save(order);
            //create orderDetail
            List<Attribute> attributeEntities = attributeRepository.findAllByIdIn(
                    orderDtoRequest.getOrderDetails().stream().map(OrderDetail::getAttributeId).collect(Collectors.toSet())
            );
            Map<Long, Attribute> attributeEntityMap = attributeEntities.stream().collect(Collectors.toMap(
                    Attribute::getId, Function.identity()
            ));

            for (OrderDetail orderDetail : orderDtoRequest.getOrderDetails()) {
                Attribute attribute = attributeEntityMap.get(orderDetail.getAttributeId());
                if (attribute.getStock() < orderDetail.getQuantity()) {
                    throw new RuntimeException("Sản phẩm đã hết hàng hoặc không đủ số lượng.");
                }
                attribute.setStock(attribute.getStock() - orderDetail.getQuantity());
                attribute.setCache(attribute.getCache() + orderDetail.getQuantity());

                attributeRepository.save(attribute);
                orderDetail.setOrderId(order.getId());
                orderDetailRepository.save(orderDetail);
                if (Objects.nonNull(orderDtoRequest.getAccountId())) {
                    CartItem cartItem = cartItemRepository
                            .findCartItemByAccountIdAndAttributeId(orderDtoRequest.getAccountId(), orderDetail.getAttributeId());
                    cartItem.setQuantity(0L);
                    cartItem.setIsActive(Boolean.FALSE);
                    cartItemRepository.save(cartItem);
                }
            }

            // send notification
//            CompletableFuture.runAsync(() -> {
//                try {
//                    sendNotification(order);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
            try {
                sendNotification(order);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return orderMapper.getResponseByEntity(order);
        } catch (OptimisticLockException | StaleStateException exception) {
            return new OrderDtoResponse();
        }
    }

    @Override
    public OrderDtoResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        OrderStatus orderStatus = orderStatusRepository.findById(order.getOrderStatusId())
                .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));

        OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
        Voucher voucher = new Voucher();
        if(Objects.nonNull(order.getVoucherId())){
             voucher = voucherRepository.findById(order.getVoucherId()).orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
        }
        orderDtoResponse.setOrderStatusName(orderStatus.getName());
        orderDtoResponse.setDiscount(voucher.getDiscount());
        orderDtoResponse.setShipDate(order.getShipDate());
        return orderDtoResponse;
    }

    @Override
    public List<OrderDetailResponse> getOrderDetail(Long orderId) {
        List<OrderDetail> orderDetailEntities = orderDetailRepository.findAllByOrderId(orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        List<Attribute> attributeEntities = attributeRepository.findAllByIdIn(orderDetailEntities
                .stream()
                .map(OrderDetail::getAttributeId)
                .collect(Collectors.toList()));
        List<Product> productList = productRepository.findAllByIdIn(attributeEntities.stream().map(Attribute::getProductId).collect(Collectors.toList()));
        Map<Long, Image> imageMap = imageRepository.findAllByProductIdIn(productList.stream().map(Product::getId).collect(Collectors.toList()))
                .stream()
                .filter(image -> image.getName().equals("main"))
                .collect(Collectors.toMap(Image::getProductId, Function.identity()));

        Map<Long, Attribute> attributeMap = attributeEntities.stream().collect(Collectors.toMap(
                Attribute::getId, Function.identity()
        ));
        Map<Long, Voucher> voucherMap = new HashMap<>();
        if(Objects.nonNull(order.getVoucherId())){
            Voucher voucher= voucherRepository.findById(order.getVoucherId()).orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
            voucherMap.put(voucher.getId(), voucher);
        }
        return orderDetailEntities.stream().map(
                orderDetailEntity -> OrderDetailResponse
                        .builder()
                        .id(orderDetailEntity.getId())
                        .quantity(orderDetailEntity.getQuantity())
                        .image(imageMap.get(attributeMap.get(orderDetailEntity.getAttributeId()).getProductId()).getImageLink())
                        .sellPrice(orderDetailEntity.getSellPrice())
                        .originPrice(orderDetailEntity.getOriginPrice())
                        .orderId(orderDetailEntity.getOrderId())
                        .attributeSize(attributeMap.get(orderDetailEntity.getAttributeId()).getSize())
                        .attribute(attributeMapper.getResponseFromEntity(attributeMap.get(orderDetailEntity.getAttributeId())))
                        .orderStatusName(orderStatusRepository.findById(order.getOrderStatusId()).orElseThrow(() ->
                                new RuntimeException(CodeAndMessage.ERR3)).getName())
                        .discount(Objects.nonNull(voucherMap.get(order.getVoucherId()))? voucherMap.get(order.getVoucherId()).getDiscount(): null)
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<OrderStatus> getAllOrderStatus() {
        return orderStatusRepository.findAll();
    }

    @Override
    public Page<OrderDtoResponse> getAllOrders(Long accountId, Long orderStatusId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        if (orderStatusId == 0) {
            Page<OrderDtoResponse> orderEntities = orderRepository.findAllByAccountId(accountId, sortedPageable).map(orderMapper::getResponseByEntity);
            List<OrderStatus> orderStatusEntities = orderStatusRepository.findAllByIdIn(orderEntities.stream().map(OrderDtoResponse::getOrderStatusId).collect(Collectors.toList()));
            Map<Long, OrderStatus> orderStatusEntityMap = orderStatusEntities.stream()
                    .collect(Collectors.toMap(OrderStatus::getId, Function.identity()));
            return orderEntities.map(
                    orderDtoResponse -> {
                        orderDtoResponse.setOrderStatusName(orderStatusEntityMap.get(orderDtoResponse.getOrderStatusId()).getName());
                        return orderDtoResponse;
                    }
            );

        }
        Page<Order> orderEntityList = orderRepository.findAllByAccountIdAndOrderStatusId(accountId, orderStatusId, sortedPageable);
        Page<OrderDtoResponse> orderEntities = orderEntityList.map(orderMapper::getResponseByEntity);
        List<OrderStatus> orderStatusEntities = orderStatusRepository.findAllByIdIn(orderEntities.stream().map(OrderDtoResponse::getOrderStatusId).collect(Collectors.toList()));
        Map<Long, OrderStatus> orderStatusEntityMap = orderStatusEntities.stream()
                .collect(Collectors.toMap(OrderStatus::getId, Function.identity()));
        return orderEntities.map(
                orderDtoResponse -> {
                    orderDtoResponse.setOrderStatusName(orderStatusEntityMap.get(orderDtoResponse.getOrderStatusId()).getName());
                    return orderDtoResponse;
                }
        );
    }

    @Override
    @Transactional
    public OrderDtoResponse cancelOrder(CancelOrderRequest cancelOrderRequest) {
        Order order = orderRepository.findById(cancelOrderRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        OrderStatus orderStatus = orderStatusRepository.findById(order.getOrderStatusId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if (orderStatus.getName().equals(OrderStatusEnum.IS_DELIVERY.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đang được vận chuyển,không thể hủy ");
        } else if (orderStatus.getName().equals(OrderStatusEnum.CANCELED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được hủy.");
        } else if (orderStatus.getName().equals(OrderStatusEnum.DELIVERED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được giao thành công, không thể hủy");
        }
        OrderStatus orderStatusCancel = orderStatusRepository.findByName(OrderStatusEnum.CANCELED.getValue());
        order.setOrderStatusId(orderStatusCancel.getId());
        order.setDescription(cancelOrderRequest.getDescription());
        order = orderRepository.save(order);

        Collection<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
        for (OrderDetail orderDetail : orderDetails) {
            Attribute attribute = attributeRepository.findById(orderDetail.getAttributeId()).orElseThrow(
                    () -> new RuntimeException(CodeAndMessage.ERR3)
            );
            attribute.setStock(attribute.getStock() + orderDetail.getQuantity());
            attribute.setCache(attribute.getCache() - orderDetail.getQuantity());
            attributeRepository.save(attribute);
        }
        if (Objects.nonNull(order.getVoucherId())) {
            Voucher voucher = voucherRepository.findById(order.getVoucherId()).orElseThrow(
                    () -> new RuntimeException(CodeAndMessage.ERR3)
            );
            if (voucher != null) {
                voucher.setCount(voucher.getCount()+1L);
                voucher.setIsActive(Boolean.TRUE);
                voucherRepository.save(voucher);
            }
        }
        Notification notification = Notification.builder()
                .read(Boolean.FALSE)
                .deliver(Boolean.FALSE)
                .type(2L)
                .content(String.format("Đơn hàng %s vừa hủy, kiểm tra ngay nào", order.getId()))
                .orderId(order.getId())
                .build();
        notificationRepository.save(notification);
        return orderMapper.getResponseByEntity(order);
    }

    @Override
    public List<CountResponse> getCountOrderByStatus() {
        List<Order> orderList = orderRepository.findAll();
        Map<Long, List<Order>> orderMap = orderList.stream().collect(Collectors.groupingBy(
                Order::getOrderStatusId, Collectors.mapping(Function.identity(), Collectors.toList())
        ));
        Map<Long, OrderStatus> statusEntityMap = orderStatusRepository.findAllByIdIn(orderList.stream().map(Order::getOrderStatusId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(OrderStatus::getId, Function.identity()));
        List<CountResponse> countResponses = new ArrayList<>();
        orderMap.forEach(
                (statusId, orderEntities) -> {
                    CountResponse countResponse = CountResponse.builder()
                            .name(statusEntityMap.get(statusId).getName())
                            .count((long) orderMap.get(statusId).size())
                            .build();
                    countResponses.add(countResponse);
                }
        );
        return countResponses;
    }

    @Override
    public Long countOrder() {
        return orderRepository.count();
    }

    @Override
    public List<YearSynthesis> getReportYear() {
        List<YearSynthesis> yearSyntheses = new ArrayList<>();
        List<Order> orderEntities = orderRepository.findAll();
        OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.DELIVERED.getValue());
        Map<Integer, List<Order>> yearOrderEntities = orderEntities.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreateDate().getYear() // Lấy năm từ createDate và nhóm theo năm
                ));
        yearOrderEntities.forEach(
                (year, orderList) -> {
                    List<Order> countOrderEntities = orderList.stream().filter(
                            order -> orderStatus.getId().equals(order.getOrderStatusId())
                    ).collect(Collectors.toList());
                    Double total = 0d;
                    for (Order order : countOrderEntities) {
                        total += order.getTotal();
                    }
                    YearSynthesis synthesis = YearSynthesis.builder()
                            .year((long) year)
                            .count((long) countOrderEntities.size())
                            .total(total)
                            .build();
                    yearSyntheses.add(synthesis);
                }
        );
        return yearSyntheses;
    }

    @Override
    public Page<ProductReport> getReportByProduct(Pageable pageable) {
        OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.DELIVERED.getValue());
        List<Order> orderEntities = orderRepository.findAllByOrderStatusId(orderStatus.getId());
        List<OrderDetail> orderDetailEntities = orderDetailRepository.findAllByOrderIdIn(orderEntities.stream()
                .map(Order::getId).collect(Collectors.toList()));
        Map<Long, List<OrderDetail>> orderAttributeMap = orderDetailEntities.stream().collect(Collectors.groupingBy(
                OrderDetail::getAttributeId, Collectors.mapping(Function.identity(), Collectors.toList())
        ));

        List<Attribute> attributeEntities = attributeRepository.findAllByIdIn(orderDetailEntities.stream()
                .map(OrderDetail::getAttributeId).collect(Collectors.toSet()));
        Map<Long, List<Attribute>> productAttributeMap = attributeEntities.stream().collect(Collectors.groupingBy(
                Attribute::getProductId, Collectors.mapping(Function.identity(), Collectors.toList())
        ));
        List<Product> productEntities = productRepository.findAllByIdIn(attributeEntities.stream().map(Attribute::getProductId)
                .collect(Collectors.toSet()));
        List<ProductReport> productReports = new ArrayList<>();
        for (Product product : productEntities) {
            List<Attribute> attributeList = productAttributeMap.get(product.getId());
            double totalAmount = 0d;
            long orderCount = 0L;
            long quantityProduct = 0L;
            for (Attribute attribute : attributeList) {
                List<OrderDetail> orderDetailList = orderAttributeMap.get(attribute.getId());
                for (OrderDetail orderDetail : orderDetailList) {
                    totalAmount += orderDetail.getQuantity() * orderDetail.getSellPrice();
                    quantityProduct += orderDetail.getQuantity();
                }
                orderCount = orderDetailList.stream().map(OrderDetail::getOrderId).collect(Collectors.toSet()).size();
            }
            ProductReport productReport = ProductReport.builder()
                    .id(product.getId())
                    .amount(totalAmount)
                    .name(product.getName())
                    .count(orderCount)
                    .quantity(quantityProduct)
                    .build();
            productReports.add(productReport);
        }

        List<ProductReport> sortedReports = productReports.stream()
                .sorted(Comparator.comparing(ProductReport::getId))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedReports.size());
        List<ProductReport> paginatedReports = sortedReports.subList(start, end);

        return new PageImpl<>(paginatedReports, pageable, sortedReports.size());
    }

    @Override
    public Page<OrderDtoResponse> getOrderByYearAndMonth(Long id, Long year, Long month, Pageable pageable) {
        Page<Order> orders;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));

        if (id == 0L) {
            orders = orderRepository.findOrderByYearAndMonth(Math.toIntExact(year), Math.toIntExact(month), sortedPageable);
        } else {
            orders = orderRepository.findOrderByOrderStatusAndYearAndMonth(id, Math.toIntExact(year), Math.toIntExact(month), sortedPageable);
        }
        return orders.map(orderMapper::getResponseByEntity);
    }

    @Override
    public Page<OrderDtoResponse> getOrderByProduct(Long id, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        Page<Order> orders = orderRepository.findOrderByProduct(id, sortedPageable);
        return orders.map(order -> {
            OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
            OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                    .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
            orderDtoResponse.setOrderStatusName(orderStatus.getName());
            return orderDtoResponse;});
    }

    @Override
    public List<MonthSynthesis> getReportByMonth(Long year) {
        return orderRepository.reportAmountMonth(Math.toIntExact(year));
    }

    @Override
    @Transactional
    public OrderDtoResponse update(OrderUpdateRequest orderUpdateRequest) {
        Order order = orderRepository.findById(orderUpdateRequest.getOrderId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        order.setIsPending(orderUpdateRequest.getIsPending());
        order.setAddress(orderUpdateRequest.getAddress());
        order.setEmail(orderUpdateRequest.getEmail());
        order.setFullName(orderUpdateRequest.getFullname());
        order.setNote(orderUpdateRequest.getNote());
        order.setPhone(orderUpdateRequest.getPhone());
        order.setModifyDate(LocalDate.now());
        orderRepository.save(order);
        return orderMapper.getResponseByEntity(order);
    }

    @Override
    @Transactional
    public OrderDtoResponse cancelOrderAdmin(UpdateStatusOrderRequest orderUpdateRequest) {
        Order order = orderRepository.findById(orderUpdateRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        Long flag = order.getOrderStatusId();
        OrderStatus orderStatus = orderStatusRepository.findById(flag).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if (orderStatus.getName().equals(OrderStatusEnum.DELIVERED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được giao thành công");
        } else if (orderStatus.getName().equals(OrderStatusEnum.CANCELED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã hủy");
        } else {
            OrderStatus orderStt = orderStatusRepository.findByName(OrderStatusEnum.CANCELED.getValue());
            order.setOrderStatusId(orderStt.getId());
            order.setDescription(orderUpdateRequest.getDescription());
            order.setModifyDate(LocalDate.now());
            List<OrderDetail> list = orderDetailRepository.findAllByOrderId(order.getId());
            for (OrderDetail o : list) {
                Attribute attribute = attributeRepository.findById(o.getAttributeId()).orElseThrow(
                        () -> new RuntimeException(CodeAndMessage.ERR3)
                );
                attribute.setCache(attribute.getCache() - o.getQuantity());
                attribute.setStock(attribute.getStock() + o.getQuantity());
                attributeRepository.save(attribute);
            }
           if(Objects.nonNull(order.getVoucherId())){
               Voucher voucher = voucherRepository.findById(order.getVoucherId()).orElseThrow(
                       () -> new RuntimeException(CodeAndMessage.ERR3)
               );
               if (voucher != null) {
                   voucher.setCount(1L);
                   voucher.setIsActive(Boolean.TRUE);
                   voucherRepository.save(voucher);
               }
           }
            orderRepository.save(order);
            return orderMapper.getResponseByEntity(order);
        }
    }

    @Override
    @Transactional
    public OrderDtoResponse updateProcess(UpdateStatusOrderRequest orderUpdateRequest) {
        Order order = orderRepository.findById(orderUpdateRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        OrderStatus orderStatus = orderStatusRepository.findById(order.getOrderStatusId())
                .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));

        if (orderStatus.getName().equals(OrderStatusEnum.WAIT_ACCEPT.getValue())) {
            OrderStatus orderStt = orderStatusRepository.findByName(OrderStatusEnum.IS_LOADING.getValue());
            order.setOrderStatusId(orderStt.getId());
            order.setModifyDate(LocalDate.now());
            orderRepository.saveAndFlush(order);
            return orderMapper.getResponseByEntity(order);
        } else if (orderStatus.getName().equals(OrderStatusEnum.IS_LOADING.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được chấp nhận");
        } else if (orderStatus.getName().equals(OrderStatusEnum.IS_DELIVERY.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được vận chuyển ");
        } else if (orderStatus.getName().equals(OrderStatusEnum.DELIVERED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được giao thành công");
        } else {
            throw new RuntimeException(("ERR10-Đơn hàng đã hủy"));
        }
    }

    @Override
    @Transactional
    public OrderDtoResponse updateShipment(UpdateStatusOrderRequest updateStatusOrderRequest) {
        Order order = orderRepository.findById(updateStatusOrderRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
            OrderStatus orderStt = orderStatusRepository.findByName(OrderStatusEnum.IS_DELIVERY.getValue());
            order.setOrderStatusId(orderStt.getId());
            order.setShipment(updateStatusOrderRequest.getShipment());
            order.setCode(updateStatusOrderRequest.getCode());
            order.setShipDate(updateStatusOrderRequest.getShipDate());
            order.setModifyDate(LocalDate.now());
            orderRepository.saveAndFlush(order);
            return orderMapper.getResponseByEntity(order);
    }

    @Override
    @Transactional
    public OrderDtoResponse updateSuccess(UpdateStatusOrderRequest updateStatusOrderRequest) {

        Order order = orderRepository.findById(updateStatusOrderRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );

        OrderStatus orderStt = orderStatusRepository.findById(order.getOrderStatusId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );

        if (orderStt.getName().equals(OrderStatusEnum.WAIT_ACCEPT.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đang chờ xác nhận");
        } else if (orderStt.getName().equals(OrderStatusEnum.IS_LOADING.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng cần xác nhận vận chuyển");
        } else if (orderStt.getName().equals(OrderStatusEnum.IS_DELIVERY.getValue())) {
            OrderStatus orderStatus = orderStatusRepository.findByName(OrderStatusEnum.DELIVERED.getValue());
            order.setOrderStatusId(orderStatus.getId());
            order.setModifyDate(LocalDate.now());
            order.setIsPending(true);
            List<OrderDetail> list = orderDetailRepository.findAllByOrderId(order.getId());
            for (OrderDetail o : list) {
                Attribute attribute = attributeRepository.findById(o.getAttributeId()).orElseThrow(
                        () -> new RuntimeException(CodeAndMessage.ERR3)
                );
                attribute.setCache(attribute.getCache() - o.getQuantity());
                attributeRepository.save(attribute);
            }
            if (order.getTotal() > 1000000) {
                Voucher voucher = new Voucher();
                voucher.setCode(generateCode());
                voucher.setIsActive(Boolean.TRUE);
                voucher.setCreateDate(LocalDate.now());
                voucher.setCount(1L);
                voucher.setExpireDate(LocalDate.now().plusYears(1));
                if (order.getTotal() >= 3000000) {
                    voucher.setDiscount(30L);
                } else if (order.getTotal() >= 2000000) {
                    voucher.setDiscount(20L);
                } else {
                    voucher.setDiscount(10L);
                }
                voucher = voucherRepository.save(voucher);
                try {
                    MailUtil.sendEmail(voucher, order);
                } catch (MessagingException e) {
                    System.out.println("Can't send an email.");
                }
            }
            orderRepository.save(order);
            return orderMapper.getResponseByEntity(order);
        } else if (orderStt.getName().equals(OrderStatusEnum.DELIVERED.getValue())) {
            throw new RuntimeException("ERR10-Đơn hàng đã được giao thành công");
        } else {
            throw new RuntimeException("ERR10-Đơn hàng đã hủy");
        }
    }

    @Override
    public Page<OrderDtoResponse> getPage(Long id,String payment, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc( "createDate"),Sort.Order.desc("id")));
        if (id != 0L) {
            OrderStatus orderStatus = orderStatusRepository.findById(id).orElseThrow(
                    () -> new RuntimeException(CodeAndMessage.ERR3)
            );
            if (orderStatus == null) {
                return orderRepository.findAllByPayment(payment,sortedPageable).map(orderMapper::getResponseByEntity);
            }
            if(Objects.nonNull(payment) && !payment.isEmpty()&& !payment.equals("null")){
                return orderRepository.findAllByOrderStatusIdAndPayment(id,payment, sortedPageable).map(orderMapper::getResponseByEntity);
            }else{
                return orderRepository.findAllByOrderStatusId(id, sortedPageable).map(orderMapper::getResponseByEntity);
            }
        } else {
           if(Objects.nonNull(payment) && !payment.isEmpty() && !payment.equals("null")){
               Page<Order> orders = orderRepository.findAllByPayment(payment,sortedPageable);
               return orders.map(order -> {
                   OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                   OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                           .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                   orderDtoResponse.setOrderStatusName(orderStatus.getName());
                   return orderDtoResponse;});
           }else{
               Page<Order> orders = orderRepository.findAll(sortedPageable);
               return orders.map(order -> {
                   OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                   OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                           .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                   orderDtoResponse.setOrderStatusName(orderStatus.getName());
                   return orderDtoResponse;});
           }
        }
    }

    @Override
    public Page<OrderDtoResponse> getOrderByPayment(String payment, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc( "createDate"),Sort.Order.desc("id")));
        if (Objects.nonNull(payment) && !payment.equals("null")) {
            return orderRepository.findAllByPayment(payment, sortedPageable).map(order -> {
                OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                        .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                orderDtoResponse.setOrderStatusName(orderStatus.getName());
                return orderDtoResponse;});
        } else {
            Page<Order> orders = orderRepository.findAllByOrderByCreateDateDesc(sortedPageable);
            return orders.map(order -> {
                OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                        .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                orderDtoResponse.setOrderStatusName(orderStatus.getName());
                return orderDtoResponse;
            });
        }
    }

    @Override
    public Page<OrderDtoResponse> getOrderBetweenDate(Long id, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc( "createDate"),Sort.Order.desc("id")));
        if (id.equals(0L)) {
            return orderRepository.findOrderBetweenDate(fromDate, toDate, sortedPageable).map(order -> {
                OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                        .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                orderDtoResponse.setOrderStatusName(orderStatus.getName());
                return orderDtoResponse;});
        }
        return orderRepository.findOrderByOrderStatusBetweenDate(id, fromDate, toDate, sortedPageable).map(
                order -> {
                    OrderDtoResponse orderDtoResponse = orderMapper.getResponseByEntity(order);
                    OrderStatus orderStatus = orderStatusRepository.findById(orderDtoResponse.getOrderStatusId())
                            .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
                    orderDtoResponse.setOrderStatusName(orderStatus.getName());
                    return orderDtoResponse;}
        );
    }

    private void sendNotification(Order order) {
        Notification notification = Notification
                .builder()
                .type(1L)
                .orderId(order.getId())
                .content(String.format("Đơn hàng số %s vừa được tạo, xác nhận ngay nào", order.getId()))
                .deliver(Boolean.FALSE)
                .read(Boolean.FALSE)
                .build();
        notificationRepository.save(notification);
        try {
            MailUtil.sendEmailOrder(order);
        } catch (MessagingException e) {
            System.out.println("Can't send an email.");
        }
    }

    private String generateCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
