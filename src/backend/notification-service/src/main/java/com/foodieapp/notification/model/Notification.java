package com.foodieapp.notification.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "notifications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String title;
    @Column(length = 1000) private String message;
    @Enumerated(EnumType.STRING) @Builder.Default private NotificationType type = NotificationType.ACCOUNT;
    @Builder.Default private boolean isRead = false;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
}
