package com.foodieapp.admin.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "admins")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String name;
    @Column(unique = true, nullable = false) private String email;
    @Enumerated(EnumType.STRING) @Builder.Default private AdminLevel level = AdminLevel.MODERATOR;
    @Builder.Default private boolean isActive = true;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
