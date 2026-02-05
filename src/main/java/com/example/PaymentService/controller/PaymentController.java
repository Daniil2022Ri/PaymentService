package com.example.PaymentService.controller;

import com.example.PaymentService.customExeption.PaymentAlreadyProcessedException;
import com.example.PaymentService.customExeption.PaymentNotFoundException;
import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponseDto;
import com.example.PaymentService.model.Payment;
import com.example.PaymentService.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payment API", description = "Operations related to payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a payment request and processes it asynchronously")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Parameter(description = "Payment request details", required = true)
            @RequestBody @Valid PaymentRequest request) {
        log.info("Creating payment for account: {}, amount: {}", request.getAccountId(), request.getAmount());
        Payment payment = paymentService.createPayment(request);
        PaymentResponseDto responseDto = paymentToResponseDto(payment);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment information by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> getPayment(
            @Parameter(description = "ID of the payment to retrieve", required = true)
            @PathVariable Long id) {
        return paymentService.findById(id)
                .map(this::paymentToResponseDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a payment", description = "Cancels a payment if it hasn't been processed yet")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "409", description = "Payment already processed")
    })
    public ResponseEntity<Void> cancelPayment(
            @Parameter(description = "ID of the payment to cancel", required = true)
            @PathVariable Long id) {
        try {
            paymentService.cancelPayment(id);
            return ResponseEntity.noContent().build();
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PaymentAlreadyProcessedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private PaymentResponseDto paymentToResponseDto(com.example.PaymentService.model.Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getAccountId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
