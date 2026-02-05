package com.example.PaymentService.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessage {

    private Long id;
    private Long accountId;
    private BigDecimal amount;

}
