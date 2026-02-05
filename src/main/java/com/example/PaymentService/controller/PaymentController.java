package com.example.PaymentService.controller;

import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.model.Payment;
import com.example.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        log.info("Creating payment for account: {}, amount: {}", request.getAccountId(), request.getAmount());
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        // Здесь можно добавить получение из репозитория
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        // Логика отмены платежа
        return ResponseEntity.noContent().build();
    }
}
