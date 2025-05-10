package com.example.intershop.repository;

import com.example.intershop.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}

