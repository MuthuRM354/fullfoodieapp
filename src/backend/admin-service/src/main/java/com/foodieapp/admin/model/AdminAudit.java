package com.foodieapp.admin.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "admin_audits")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long adminId;
    @Column(nullable = false) private String action;
    @Column(nullable = false) private String resource;
    private Long resourceId;
    @Column(length = 2000) private String details;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
}
