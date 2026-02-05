package com.example.PaymentService.dto;


import com.example.PaymentService.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
