package com.foodieapp.tracking.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "location_history")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long orderId;
    private Long deliveryPartnerId;
    private Double latitude;
    private Double longitude;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime recordedAt;
}
