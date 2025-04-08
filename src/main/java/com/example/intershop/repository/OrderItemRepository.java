package com.example.intershop.repository;

import com.example.intershop.model.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
}
