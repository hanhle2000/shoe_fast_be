package org.graduate.shoefastbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.order.*;
import org.graduate.shoefastbe.dto.product.ProductReport;
import org.graduate.shoefastbe.entity.OrderStatus;
import org.graduate.shoefastbe.service.order.OrderService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "Tạo mới order")
    OrderDtoResponse createOrder(@RequestBody OrderDtoRequest orderDtoRequest) {
        return orderService.createOrder(orderDtoRequest);
    }

    @GetMapping()
    @Operation(summary = "Lấy thông tin đơn hàng")
    OrderDtoResponse getOrderById(@RequestParam Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/order-detail")
    @Operation(summary = "Lấy thông tin chi tiết đơn hàng")
    List<OrderDetailResponse> getOrderDetail(@RequestParam Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    @GetMapping("/order-status")
    @Operation(summary = "Lấy thông tin chi tiết trạng thái đơn hàng")
    List<OrderStatus> getAllOrderStatus() {
        return orderService.getAllOrderStatus();
    }

    @GetMapping("/list")
    @Operation(summary = "Lấy danh sách đơn hàng")
    Page<OrderDtoResponse> getAllOrder(@RequestParam Long accountId,
                                       @RequestParam Long orderStatusId,
                                       @ParameterObject Pageable pageable) {
        return orderService.getAllOrders(accountId, orderStatusId, pageable);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Xóa đơn hàng")
    OrderDtoResponse cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest) {
        return orderService.cancelOrder(cancelOrderRequest);
    }

    // admin
    @GetMapping("/list/count")
    @Operation(summary = "Lấy số lượng đơn hàng")
    List<CountResponse> getAllOrderCount() {
        return orderService.getCountOrderByStatus();
    }

    @GetMapping("/count")
    @Operation(summary = "Đếm số lượng đơn hàng")
    Long getCountOrder() {
        return orderService.countOrder();
    }

    @GetMapping("/synthesis/year")
    @Operation(summary = "Lấy thống kê theo năm")
    List<YearSynthesis> getYearSynthesis() {
        return orderService.getReportYear();
    }

    @GetMapping("/synthesis/product")
    @Operation(summary = "Lấy thống kê theo sản phẩm")
    Page<ProductReport> getReportProduct(@ParameterObject Pageable pageable) {
        return orderService.getReportByProduct(pageable);
    }

    @GetMapping("/synthesis/order-by-year-month")
    Page<OrderDtoResponse> getOrderByYearAndMonth(@RequestParam Long id,
                                                  @RequestParam Long year,
                                                  @RequestParam Long month,
                                                  @ParameterObject Pageable pageable) {
        return orderService.getOrderByYearAndMonth(id, year, month, pageable);
    }

    @GetMapping("/synthesis/order-by-product")
    Page<OrderDtoResponse> getOrderByProduct(@RequestParam Long id,
                                             @ParameterObject Pageable pageable) {
        return orderService.getOrderByProduct(id, pageable);
    }

    @GetMapping("/synthesis/amount-month")
    List<MonthSynthesis> getReportAmountMonth(@RequestParam Long year) {
        return orderService.getReportByMonth(year);
    }

    @PostMapping("/update")
    OrderDtoResponse update(@RequestBody OrderUpdateRequest orderUpdateRequest) {
        return orderService.update(orderUpdateRequest);
    }

    @PostMapping("/admin/cancel-order")
    OrderDtoResponse cancelOrderAdmin(@RequestBody UpdateStatusOrderRequest updateRequest) {
        return orderService.cancelOrderAdmin(updateRequest);
    }

    @PostMapping("/admin/update-process")
    OrderDtoResponse updateProcess(@RequestBody UpdateStatusOrderRequest updateRequest) {
        return orderService.updateProcess(updateRequest);
    }

    @PostMapping("/admin/update-shipment")
    OrderDtoResponse updateShipment(@RequestBody UpdateStatusOrderRequest updateStatusOrderRequest) {
        return orderService.updateShipment(updateStatusOrderRequest);
    }

    @PostMapping("/admin/update-success")
    OrderDtoResponse updateSuccess(@RequestBody UpdateStatusOrderRequest updateStatusOrderRequest) {
        return orderService.updateSuccess(updateStatusOrderRequest);
    }

    @GetMapping("/page-admin")
    Page<OrderDtoResponse> getPage(@RequestParam(required = false) Long status,
                                   @RequestParam(required = false) String payment,
                                   @ParameterObject Pageable pageable) {
        return orderService.getPage(status,payment, pageable);
    }
    @GetMapping("/payment")
    Page<OrderDtoResponse> getOrderByPayment(@RequestParam(required = false) String payment,
                                             @ParameterObject Pageable pageable) {
        return orderService.getOrderByPayment(payment, pageable);
    }
    @GetMapping("/admin/page-orders-between-date")
    Page<OrderDtoResponse> getOrderBetweenDate(@RequestParam Long id,
                                               @RequestParam String from,
                                               @RequestParam String to,
                                               @ParameterObject Pageable pageable) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fromDate = LocalDate.parse(from, dtf);
        LocalDate toDate = LocalDate.parse(to, dtf);
        return orderService.getOrderBetweenDate(id, fromDate, toDate, pageable);
    }
}
