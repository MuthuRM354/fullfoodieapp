package com.foodieapp.notification.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service @Slf4j
public class PushNotificationService {
    public void send(Long userId, String title, String message) {
        log.info("PUSH -> UserId: {} | Title: {} | Message: {}", userId, title, message);
    }
    public void sendOrderUpdate(Long userId, Long orderId, String status) {
        send(userId, "Order Update", "Your order #" + orderId + " is now " + status);
    }
}
