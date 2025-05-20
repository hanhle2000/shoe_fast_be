package org.graduate.shoefastbe.controller;

import lombok.RequiredArgsConstructor;
import org.graduate.shoefastbe.base.ResponseObject;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.entity.Order;
import org.graduate.shoefastbe.repository.OrderRepository;
import org.graduate.shoefastbe.service.PaymentService;
import org.graduate.shoefastbe.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request, @RequestParam Long orderId) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request, orderId));
    }
    @PutMapping("/ship-code")
    public ResponseEntity<String> paymentShipCode(@RequestParam Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));

        if (order != null) {
            order.setPayment("Thanh toán khi giao hàng(COD)");
            orderRepository.save(order);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/vn-pay-callback")
    public void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String vnpTransactionStatus = request.getParameter("vnp_TransactionStatus");
        String vnpOrderId = request.getParameter("vnp_OrderInfo");

        if ("00".equals(vnpTransactionStatus)) {
            Long orderId = Long.parseLong(vnpOrderId);
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));

            if (order != null) {
                order.setIsPending(Boolean.TRUE);
                order.setPayment("CHUYỂN KHOẢN QUA VNPAY");
                order.setOrderStatusId(2L);
                orderRepository.save(order);
                response.sendRedirect("http://localhost:3000/order/detail/" + orderId);
//                response.sendRedirect("https://shoe-fast-fe.onrender.com/order/detail/" + orderId);
            }
        } else {
            response.sendRedirect("http://localhost:3000/");
            response.sendRedirect("https://shoe-fast-fe.onrender.com/");
        }
    }


}
