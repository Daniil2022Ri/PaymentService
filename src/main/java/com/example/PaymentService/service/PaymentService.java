package com.example.PaymentService.service;

import com.example.PaymentService.config.RabbitConfig;
import com.example.PaymentService.customExeption.PaymentAlreadyProcessedException;
import com.example.PaymentService.customExeption.PaymentNotFoundException;
import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.message.PaymentMessage;
import com.example.PaymentService.model.Payment;
import com.example.PaymentService.model.PaymentStatus;
import com.example.PaymentService.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    public Payment createPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setAccountId(request.getAccountId());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        PaymentMessage message = new PaymentMessage();
        message.setId(payment.getId());
        message.setAccountId(payment.getAccountId());
        message.setAmount(payment.getAmount());

        rabbitTemplate.convertAndSend(RabbitConfig.PAYMENT_EXCHANGE, RabbitConfig.PAYMENT_ROUTING_KEY, message);

        return payment;
    }

    public boolean isProcessed(Long paymentId) {
        return paymentRepository.existsByIdAndProcessedTrue(paymentId);
    }

    public void markProcessed(PaymentMessage msg) {
        Payment payment = paymentRepository.findById(msg.getId()).orElseThrow();
        payment.setProcessed(true);
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
    }
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public void cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Cannot cancel: payment not found"));

        if (payment.getProcessed()) {
            throw new PaymentAlreadyProcessedException("Cannot cancel processed payment");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }
}
