# Intershop: Витрина интернет-магазина

проект на Java + Spring WebFlux. Включает витрину товаров (`store-service`) и платёжный сервис (`payment-service`) с кешированием Redis, OpenAPI-интеграцией и Docker-окружением.
Дополнительно используется:
- Redis для кеширования товаров
- Keycloak для OpenID авторизации
- OpenAPI для генерации клиента и сервера платёжного сервиса
- Docker Compose для локального запуска
- 
## Архитектура

- `store-service` — витрина с товарами и корзиной (порт `8081`)
- `payment-service` — сервис обработки платежей и получения баланса (порт `8080`)
- `Redis` — кеш товаров
- `keycloak`        Авторизация через OpenID        -8089

Проект собран как мультимодульный Maven-проект с реактивным стеком.

## Запуск

### 1. Сборка проекта

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### 2. Запуск через Docker

```bash
docker-compose up --build
```

После запуска доступны:

- Витрина: http://localhost:8081/main/items
- Redis: `localhost:6379` (в контейнере)
- Платёжный сервис API: http://localhost:8080
  Keycloak: http://localhost:8089 (user1 user1)

## Технологии

- Java 21
- Spring Boot 3.2.5
- Spring WebFlux
- Spring Data R2DBC + H2
- Spring Data Redis Reactive
- Thymeleaf
- OpenAPI + openapi-generator
- Docker + docker-compose
- Reactive WebClient

## Основной функционал

### Store-сервис (`store-service`)

- Просмотр товаров с пагинацией, сортировкой и фильтрацией
- Кеширование товаров в Redis
- Управление корзиной (добавление, удаление, изменение количества)
- HTML-интерфейс на Thymeleaf

### Payment-сервис (`payment-service`)

- `POST /pay` — списание средств
- `GET /balance/{userId}` — получение баланса пользователя
- Генерация API и моделей по OpenAPI

## 🧪 Тесты

- Интеграционные тесты (`@SpringBootTest`)
- Поддержка unit-тестов
- Тесты можно запускать через:

```bash
mvn test
```

##️ Переменные окружения

Все настройки задаются в `application.yml` (порт, Redis и т.д.)
