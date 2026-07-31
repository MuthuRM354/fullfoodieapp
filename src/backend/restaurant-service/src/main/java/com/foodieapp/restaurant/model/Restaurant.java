package com.foodieapp.restaurant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    // Frontend reads: r.cuisine  →  expose as "cuisine"
    @JsonProperty("cuisine")
    private String cuisineType;

    private String address;
    private String city;
    private String pincode;
    private String phone;
    private String email;

    // Frontend reads: r.averageRating  →  expose as "averageRating"
    @JsonProperty("averageRating")
    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer totalReviews = 0;

    // Boolean wrapper → Lombok generates getIsOpen() → Jackson serializes as "isOpen"
    @Builder.Default
    private Boolean isOpen = true;

    @Builder.Default
    private Boolean isActive = true;

    // Frontend reads: r.deliveryTime (e.g. "30-45 min")
    @Builder.Default
    private String deliveryTime = "30-45 min";

    // Frontend reads: r.minOrder (minimum order value)
    @Builder.Default
    private Double minOrder = 0.0;

    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
