package com.example.intershop.config;

import com.example.intershop.model.Item;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Item> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Item> serializer = new Jackson2JsonRedisSerializer<>(Item.class);
        RedisSerializationContext<String, Item> context = RedisSerializationContext
                .<String, Item>newSerializationContext(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
