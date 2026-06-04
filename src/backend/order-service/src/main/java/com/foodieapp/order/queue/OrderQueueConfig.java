package com.foodieapp.order.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class OrderQueueConfig {

    @Bean
    public LinkedBlockingQueue<String> orderQueue() {
        return new LinkedBlockingQueue<>(1000);
    }
}
