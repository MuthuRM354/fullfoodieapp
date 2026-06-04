package com.foodieapp.notification.service;
import com.foodieapp.notification.model.Notification;
import com.foodieapp.notification.model.NotificationType;
import com.foodieapp.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    public Notification save(Long userId, String title, String message, NotificationType type) {
        Notification n = Notification.builder().userId(userId).title(title).message(message).type(type).build();
        return repo.save(n);
    }
    public Notification save(Notification n) { return repo.save(n); }
    public List<Notification> getByUser(Long userId) { return repo.findByUserIdOrderByCreatedAtDesc(userId); }
    public Notification markRead(Long id) {
        Notification n = repo.findById(id).orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        n.setRead(true);
        return repo.save(n);
    }
    public void markAllRead(Long userId) {
        List<Notification> unread = repo.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        repo.saveAll(unread);
    }
    public void delete(Long id) { repo.deleteById(id); }
    public long countUnread(Long userId) { return repo.countByUserIdAndIsReadFalse(userId); }
}
