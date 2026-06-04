package com.foodieapp.admin.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "admin_workflows")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminWorkflow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String type;
    @Builder.Default private String status = "PENDING";
    @Column(nullable = false) private Long requestedBy;
    @Column(length = 2000) private String details;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
