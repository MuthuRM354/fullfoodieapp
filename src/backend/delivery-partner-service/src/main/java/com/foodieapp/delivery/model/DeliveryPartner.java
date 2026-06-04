package com.foodieapp.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DeliveryPartner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String phone;
    private String vehicleType;
    private String vehicleNumber;
    @Builder.Default private boolean isAvailable = false;
    @Builder.Default private boolean isActive = true;
    private Double currentLatitude;
    private Double currentLongitude;
    @Builder.Default private Double rating = 0.0;
    @Builder.Default private Integer totalDeliveries = 0;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
