package com.foodieapp.tracking.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "tracking_info")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TrackingInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long orderId;
    private Long deliveryPartnerId;
    private Double latitude;
    private Double longitude;
    @Builder.Default private String status = "PENDING";
    @UpdateTimestamp private LocalDateTime updatedAt;
}
