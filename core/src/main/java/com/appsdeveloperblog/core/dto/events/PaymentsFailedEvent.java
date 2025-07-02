package com.appsdeveloperblog.core.dto.events;

import java.util.UUID;

public class PaymentsFailedEvent {

    private UUID orderId;
    private UUID productId;
    private Integer productInteger;

    public PaymentsFailedEvent() {

    }

    public PaymentsFailedEvent(UUID orderId, UUID productId, Integer productInteger) {
        this.orderId = orderId;
        this.productId = productId;
        this.productInteger = productInteger;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getProductInteger() {
        return productInteger;
    }

    public void setProductInteger(Integer productInteger) {
        this.productInteger = productInteger;
    }
}
