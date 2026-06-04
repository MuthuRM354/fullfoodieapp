package com.foodieapp.admin.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "admin_permissions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminPermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long adminId;
    @Column(nullable = false) private String resource;
    @Column(length = 500) private String actions; // comma-separated: READ,WRITE,DELETE
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
}
