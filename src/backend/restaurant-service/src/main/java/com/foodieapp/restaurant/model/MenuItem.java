package com.foodieapp.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String category;

    // Boolean wrapper → Lombok generates getIsVeg() → Jackson serializes as "isVeg"
    // Frontend reads: item.isVeg  ✓
    @Builder.Default
    private Boolean isVeg = false;

    // Boolean wrapper → Lombok generates getIsAvailable() → Jackson serializes as "isAvailable"
    // Frontend reads: item.isAvailable  ✓
    @Builder.Default
    private Boolean isAvailable = true;

    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
