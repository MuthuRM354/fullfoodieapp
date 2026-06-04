package com.foodieapp.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "earnings")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Earnings {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long deliveryPartnerId;
    private Long orderId;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime date;
    @Builder.Default private String type = "DELIVERY_FEE";
}
