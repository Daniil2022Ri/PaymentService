package com.example.PaymentService.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EntityManager entityManager;

    public void debit(Long accountId, BigDecimal amount) {
        String key = "balance:" + accountId;
        String lockKey = "lock:" + accountId;

        try {
            // Попытка захватить лок
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(10));
            if (!lockAcquired) {
                throw new RuntimeException("Account locked for processing");
            }

            // Получаем баланс из кэша или БД
            BigDecimal currentBalance = getCachedBalance(accountId);
            if (currentBalance.compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds");
            }

            // Списываем средства
            BigDecimal newBalance = currentBalance.subtract(amount);
            updateCachedBalance(accountId, newBalance);

            // Сохраняем в БД
            Query query = entityManager.createNativeQuery(
                    "UPDATE accounts SET balance = ? WHERE id = ?");
            query.setParameter(1, newBalance);
            query.setParameter(2, accountId);
            query.executeUpdate();

        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private BigDecimal getCachedBalance(Long accountId) {
        String key = "balance:" + accountId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return new BigDecimal(cached.toString());
        }

        Query query = entityManager.createNativeQuery(
                "SELECT balance FROM accounts WHERE id = ?");
        query.setParameter(1, accountId);
        Object result = query.getSingleResult();
        BigDecimal balance = (BigDecimal) result;
        redisTemplate.opsForValue().set(key, balance, Duration.ofMinutes(10));
        return balance;
    }

    private void updateCachedBalance(Long accountId, BigDecimal balance) {
        String key = "balance:" + accountId;
        redisTemplate.opsForValue().set(key, balance, Duration.ofMinutes(10));
    }
}

