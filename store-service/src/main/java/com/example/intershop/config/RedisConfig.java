package com.example.intershop.config;

import com.example.intershop.model.Item;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public ReactiveRedisTemplate<String, Item> redisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, Item> context = RedisSerializationContext
                .<String, Item>newSerializationContext(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Item.class))
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
