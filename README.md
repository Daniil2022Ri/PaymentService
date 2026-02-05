Микросервис обработки платежей с асинхронной обработкой, кэшированием и enterprise-level security.

## 🚀 Фичи

- **REST API** для создания и управления платежами
- **JWT-аутентификация** (USER, ADMIN роли)
- **Async обработка** через RabbitMQ
- **Redis кэш** баланса (read-through/write-through)
- **SQL оптимизация** (индексы, JOIN'ы, EntityGraph)
- **Мониторинг** (Micrometer + Prometheus + Grafana)
- **Валидация запросов** (@Valid + custom constraints)
- **Custom exceptions** и global error handling
- **Полное покрытие тестами** (80%+ code coverage)
- **Docker + Docker Compose** для быстрого запуска
- **OpenAPI/Swagger** документация
- **CI/CD pipeline** (GitHub Actions)

Проект нацелен на демонстрацию навыков



    └── src
        ├── main
        │   └── java
        │       └── com
        │           └── example
        │               └── PaymentService
        │                   ├── config
        │                   │   ├── RabbitConfig.java
        │                   │   ├── RedisConfig.java
        │                   │   └── SecurityConfig.java
        │                   ├── controller
        │                   │   └── PaymentController.java
        │                   ├── customExeption
        │                   │   ├── GlobalExceptionHandler.java
        │                   │   ├── InsufficientFundsException.java
        │                   │   ├── PaymentAlreadyProcessedException.java
        │                   │   └── PaymentNotFoundException.java
        │                   ├── dto
        │                   │   ├── PaymentRequest.java
        │                   │   └── PaymentResponseDto.java
        │                   ├── listener
        │                   │   └── PaymentListener.java
        │                   ├── message
        │                   │   └── PaymentMessage.java
        │                   ├── model
        │                   │   ├── Account.java
        │                   │   ├── Payment.java
        │                   │   └── PaymentStatus.java
        │                   ├── repository
        │                   │   └── PaymentRepository.java
        │                   ├── service
        │                   │   ├── BalanceService.java
        │                   │   └── PaymentService.java
        │                   ├── PaymentServiceApplication.java
        │                   └── ServletInitializer.java
        └── test
            └── java
                └── com
                    └── example
                        └── PaymentService
                            ├── PaymentServiceApplicationTests.java
                            └── PaymentServiceIntegrationTest.java
