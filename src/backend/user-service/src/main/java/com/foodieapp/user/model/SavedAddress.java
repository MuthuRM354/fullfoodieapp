package com.foodieapp.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // e.g. "Home", "Work", "Other"
    @Column(nullable = false)
    private String label;

    @Column(nullable = false, length = 500)
    private String addressLine;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
