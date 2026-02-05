package com.example.PaymentService.repository;

import com.example.PaymentService.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByIdAndProcessedTrue(Long id);

}

