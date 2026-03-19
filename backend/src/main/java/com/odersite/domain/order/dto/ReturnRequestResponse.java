package com.odersite.domain.order.dto;

import com.odersite.domain.order.entity.ReturnRequest;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReturnRequestResponse {
    private final Integer returnId;
    private final Integer orderItemId;
    private final String type;
    private final String reason;
    private final String status;
    private final LocalDateTime requestedAt;

    public ReturnRequestResponse(ReturnRequest r) {
        this.returnId = r.getReturnId();
        this.orderItemId = r.getOrderItem().getOrderItemId();
        this.type = r.getType().name();
        this.reason = r.getReason();
        this.status = r.getStatus().name();
        this.requestedAt = r.getRequestedAt();
    }
}
