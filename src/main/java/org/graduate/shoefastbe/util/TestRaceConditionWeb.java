package org.graduate.shoefastbe.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.graduate.shoefastbe.dto.order.OrderDtoRequest;
import org.graduate.shoefastbe.entity.OrderDetail;
import org.springframework.web.client.RestTemplate;
public class TestRaceConditionWeb {
    public static void main(String[] args) {
        String apiUrl = "http://localhost:8086/api/v1/order/create";
        RestTemplate restTemplate = new RestTemplate();

        OrderDtoRequest request = OrderDtoRequest.builder()
                .fullName("Nguyen Van B")
                .phone("0987654321")
                .address("456 Đường XYZ")
                .note("Giao gấp")
                .total(300000.0)
                .email("customer@gmail.com")
                .isPending(true)
                .accountId(2L)
                .orderDetails(List.of(
                        new OrderDetail(1L, 100000.0, 9L, 90000.0, 9L, null), // attributeId = 1
                        new OrderDetail(1L, 200000.0, 9L, 180000.0, 9L, null)  // attributeId = 2
                ))
                .code("SALE2024")
                .payment("COD")
                .build();

        try {
            String testResponse = restTemplate.postForObject(apiUrl, request, String.class);
            System.out.println("Yêu cầu thử nghiệm thành công: " + testResponse);
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi yêu cầu thử nghiệm: " + e.getMessage());
        }
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    String response = restTemplate.postForObject(apiUrl, request, String.class);
                    System.out.println(response);
                } catch (Exception e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
