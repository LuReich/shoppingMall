package it.back.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import it.back.product.entity.ProductEntity;

@Entity
@Table(name = "review")
@Getter
@Setter
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String writer;
}