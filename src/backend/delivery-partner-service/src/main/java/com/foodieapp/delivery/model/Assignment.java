package com.foodieapp.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Assignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long orderId;
    @Column(nullable = false) private Long deliveryPartnerId;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime assignedAt;
    private LocalDateTime pickedAt;
    private LocalDateTime deliveredAt;
    @Enumerated(EnumType.STRING) @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
}
