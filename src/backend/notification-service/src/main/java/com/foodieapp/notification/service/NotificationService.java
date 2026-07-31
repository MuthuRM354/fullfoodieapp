package com.foodieapp.notification.service;

import com.foodieapp.notification.client.UserServiceClient;
import com.foodieapp.notification.model.Notification;
import com.foodieapp.notification.model.NotificationType;
import com.foodieapp.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repo;
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final SMSService smsService;

    /** Notification types urgent enough to also send an SMS, not just email/in-app. */
    private static final Set<NotificationType> SMS_WORTHY = Set.of(
            NotificationType.ORDER, NotificationType.ORDER_UPDATE, NotificationType.PAYMENT_FAILED);

    @Transactional
    public Notification save(Long userId, String title, String message, NotificationType type) {
        return save(userId, title, message, type, null, null);
    }

    /**
     * Persists the notification and best-effort dispatches it over email
     * (always, when we can resolve an address) and SMS (for time-sensitive
     * types). Email/phone can be supplied directly by the caller (e.g. a
     * signup flow that already has them) — otherwise they're resolved from
     * user-service by userId. Dispatch failures never fail the request; the
     * notification is already saved to the DB either way.
     */
    @Transactional
    public Notification save(Long userId, String title, String message, NotificationType type,
                              String emailOverride, String phoneOverride) {
        Notification n = Notification.builder()
                .userId(userId).title(title).message(message).type(type).build();
        Notification saved = repo.save(n);

        dispatch(userId, title, message, type, emailOverride, phoneOverride);

        return saved;
    }

    private void dispatch(Long userId, String title, String message, NotificationType type,
                           String emailOverride, String phoneOverride) {
        try {
            String email = emailOverride;
            String phone = phoneOverride;

            if (!StringUtils.hasText(email) || !StringUtils.hasText(phone)) {
                UserServiceClient.ContactInfo contact = userServiceClient.lookup(userId);
                if (contact != null) {
                    if (!StringUtils.hasText(email)) email = contact.email();
                    if (!StringUtils.hasText(phone)) phone = contact.phone();
                }
            }

            if (StringUtils.hasText(email)) {
                emailService.sendEmail(email, title, message);
            }
            if (SMS_WORTHY.contains(type) && StringUtils.hasText(phone)) {
                smsService.sendSms(phone, title + " — " + message);
            }
        } catch (Exception e) {
            // Dispatch is best-effort; the notification record itself is
            // already saved regardless of email/SMS success.
            log.warn("Notification dispatch failed for user {}: {}", userId, e.getMessage());
        }
    }

    @Transactional
    public Notification save(Notification n) {
        return repo.save(n);
    }

    public List<Notification> getByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification markRead(Long id) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        n.setRead(true);
        return repo.save(n);
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> unread = repo.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        repo.saveAll(unread);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public long countUnread(Long userId) {
        return repo.countByUserIdAndIsReadFalse(userId);
    }
}
