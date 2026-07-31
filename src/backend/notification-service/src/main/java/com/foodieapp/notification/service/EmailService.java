package com.foodieapp.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Sends real email via Gmail SMTP (Spring Mail) when MAIL_USERNAME /
 * MAIL_PASSWORD are configured. If they are not set (e.g. local dev without
 * a Gmail App Password), this falls back to logging only, so the rest of
 * the notification flow keeps working without failing.
 */
@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${notification.email.from:noreply@foodieapp.local}")
    private String fromAddress;

    @Value("${notification.email.enabled:true}")
    private boolean enabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private boolean isConfigured() {
        return enabled && StringUtils.hasText(mailUsername) && StringUtils.hasText(mailPassword);
    }

    public void sendEmail(String to, String subject, String body) {
        if (!StringUtils.hasText(to)) {
            log.debug("Skipping email '{}' — no recipient address available", subject);
            return;
        }
        if (!isConfigured()) {
            log.info("[EMAIL:log-only, MAIL_USERNAME/MAIL_PASSWORD not set] To: {} | Subject: {} | Body: {}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("EMAIL sent -> To: {} | Subject: {}", to, subject);
        } catch (MailException e) {
            // Non-fatal: notifications are still persisted to the DB even if
            // the email provider is down/misconfigured.
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendOrderConfirmation(String to, Long orderId) {
        sendEmail(to, "Order Confirmed #" + orderId, "Your order #" + orderId + " has been confirmed.");
    }

    public void sendPaymentSuccess(String to, Long orderId, String amount) {
        sendEmail(to, "Payment Successful", "Payment of " + amount + " for order #" + orderId + " was successful.");
    }
}
