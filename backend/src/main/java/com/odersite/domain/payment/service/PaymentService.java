package com.odersite.domain.payment.service;

import com.odersite.domain.order.entity.Orders;
import com.odersite.domain.order.repository.OrdersRepository;
import com.odersite.domain.payment.dto.PaymentRequest;
import com.odersite.domain.payment.dto.PaymentResponse;
import com.odersite.domain.payment.entity.Payment;
import com.odersite.domain.payment.entity.Refund;
import com.odersite.domain.payment.repository.PaymentRepository;
import com.odersite.domain.payment.repository.RefundRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrdersRepository ordersRepository;

    /**
     * 결제 준비 (F-030, F-031)
     * 실제 PG 연동 전 PENDING 상태 Payment 레코드 생성
     */
    @Transactional
    public PaymentResponse preparePayment(Integer userId, PaymentRequest request) {
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (order.getOrderState() != Orders.OrderState.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        Payment.PgProvider pgProvider = request.getPgProvider() != null
                ? Payment.PgProvider.valueOf(request.getPgProvider())
                : Payment.PgProvider.TOSSPAYMENTS;

        Payment payment = Payment.builder()
                .orders(order)
                .pgProvider(pgProvider)
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .amount(order.getFinalPrice())
                .build();

        return new PaymentResponse(paymentRepository.save(payment));
    }

    /**
     * 결제 승인 콜백 (F-032)
     * PG사 결제 완료 후 웹훅/리다이렉트로 호출
     * - TODO: 실제 PG사 SDK 연동 필요 (Tosspayments / KG Inicis)
     */
    @Transactional
    public PaymentResponse confirmPayment(Integer paymentId, String pgTransactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        // Mock: PG 트랜잭션 ID가 없으면 임시 생성
        String txId = (pgTransactionId != null) ? pgTransactionId : UUID.randomUUID().toString();
        payment.complete(txId);
        payment.getOrders().changeState(Orders.OrderState.PAID);

        log.info("Payment confirmed: paymentId={}, txId={}", paymentId, txId);
        return new PaymentResponse(payment);
    }

    /**
     * 결제 조회 (F-034)
     */
    public PaymentResponse getPayment(Integer userId, Integer orderId) {
        Payment payment = paymentRepository.findByOrders_OrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        if (!payment.getOrders().getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return new PaymentResponse(payment);
    }

    /**
     * 환불 처리 (F-033)
     * 주문 취소 또는 반품 승인 시 자동 호출
     */
    @Transactional
    public void processRefund(Integer paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .reason(reason)
                .build();
        refundRepository.save(refund);
        payment.cancel();

        // TODO: 실제 PG사 환불 API 호출 필요
        refund.complete();
        log.info("Refund processed: paymentId={}, amount={}", paymentId, payment.getAmount());
    }
}
