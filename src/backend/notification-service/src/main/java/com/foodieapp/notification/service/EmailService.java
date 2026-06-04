package com.foodieapp.notification.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service @Slf4j
public class EmailService {
    public void sendEmail(String to, String subject, String body) {
        log.info("EMAIL -> To: {} | Subject: {} | Body: {}", to, subject, body);
    }
    public void sendOrderConfirmation(String to, Long orderId) {
        sendEmail(to, "Order Confirmed #" + orderId, "Your order #" + orderId + " has been confirmed.");
    }
    public void sendPaymentSuccess(String to, Long orderId, String amount) {
        sendEmail(to, "Payment Successful", "Payment of " + amount + " for order #" + orderId + " was successful.");
    }
}
