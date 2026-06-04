package com.foodieapp.notification.queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.LinkedBlockingQueue;
@Configuration
public class NotificationQueueConfig {
    @Bean
    public LinkedBlockingQueue<String> notificationQueue() { return new LinkedBlockingQueue<>(5000); }
}
