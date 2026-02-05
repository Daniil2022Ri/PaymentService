package com.example.PaymentService.controller;

import com.example.PaymentService.customExeption.PaymentAlreadyProcessedException;
import com.example.PaymentService.customExeption.PaymentNotFoundException;
import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.model.Payment;
import com.example.PaymentService.repository.PaymentRepository;
import com.example.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        log.info("Creating payment for account: {}, amount: {}", request.getAccountId(), request.getAmount());
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        try {
            paymentService.cancelPayment(id);
            return ResponseEntity.noContent().build();
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PaymentAlreadyProcessedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
