package com.example.PaymentService.listener;

import com.example.PaymentService.config.RabbitConfig;
import com.example.PaymentService.message.PaymentMessage;
import com.example.PaymentService.service.BalanceService;
import com.example.PaymentService.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService paymentService;
    private final BalanceService balanceService;

    @RabbitListener(queues = RabbitConfig.PAYMENT_QUEUE)
    @Transactional
    public void handle(PaymentMessage msg) {
        log.info("Processing payment: {}", msg.getId());

        if (paymentService.isProcessed(msg.getId())) {
            log.warn("Payment {} already processed", msg.getId());
            return;
        }

        try {
            balanceService.debit(msg.getAccountId(), msg.getAmount());
            paymentService.markProcessed(msg);
            log.info("Payment {} processed successfully", msg.getId());
        } catch (Exception e) {
            log.error("Failed to process payment {}: {}", msg.getId(), e.getMessage());
        }
    }
}

