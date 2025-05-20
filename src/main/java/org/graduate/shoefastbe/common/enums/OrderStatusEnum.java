package org.graduate.shoefastbe.common.enums;

public enum OrderStatusEnum {
    WAIT_ACCEPT("Chờ xác nhận"),
    IS_LOADING("Đang xử lý"),
    IS_DELIVERY("Đang vận chuyển"),
    DELIVERED("Đã giao"),
    CANCELED("Đã hủy"),
    ACCEPT("Đã xác nhận");

    private final String value;
    OrderStatusEnum(String value) {
        this.value = value;
    }

    // Getter method to get the value
    public String getValue() {
        return value;
    }
}
