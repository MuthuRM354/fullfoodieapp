package com.foodieapp.review.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name = "rating_summaries")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private Long restaurantId;
    @Builder.Default private Double averageRating = 0.0;
    @Builder.Default private Integer totalReviews = 0;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
