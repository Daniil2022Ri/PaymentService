package com.example.PaymentService.controller;

import com.example.PaymentService.customExeption.PaymentAlreadyProcessedException;
import com.example.PaymentService.customExeption.PaymentNotFoundException;
import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponseDto;
import com.example.PaymentService.model.Payment;
import com.example.PaymentService.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody @Valid PaymentRequest request) {
        log.info("Creating payment for account: {}, amount: {}", request.getAccountId(), request.getAmount());
        Payment payment = paymentService.createPayment(request);
        PaymentResponseDto responseDto = paymentToResponseDto(payment);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long id) {
        return paymentService.findById(id)
                .map(this::paymentToResponseDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));
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

    private PaymentResponseDto paymentToResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getAccountId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
