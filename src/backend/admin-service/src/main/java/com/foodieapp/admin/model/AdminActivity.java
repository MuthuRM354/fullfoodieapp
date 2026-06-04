package com.foodieapp.admin.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "admin_activities")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminActivity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long adminId;
    private String activity;
    private String ipAddress;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
}
