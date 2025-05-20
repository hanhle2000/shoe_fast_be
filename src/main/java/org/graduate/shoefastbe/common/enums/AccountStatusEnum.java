package org.graduate.shoefastbe.common.enums;

public enum AccountStatusEnum {
    ACTIVE("ACTIVE"),
    IN_ACTIVE("INACTIVE");
    private final String status;

    AccountStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
