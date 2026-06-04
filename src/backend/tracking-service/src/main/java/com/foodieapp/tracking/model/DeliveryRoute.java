package com.foodieapp.tracking.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "delivery_routes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DeliveryRoute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long orderId;
    private Long deliveryPartnerId;
    private String pickupAddress;
    private String deliveryAddress;
    private Double pickupLat;
    private Double pickupLng;
    private Double deliveryLat;
    private Double deliveryLng;
    private Double estimatedDistanceKm;
    private Integer estimatedTimeMinutes;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createdAt;
}
