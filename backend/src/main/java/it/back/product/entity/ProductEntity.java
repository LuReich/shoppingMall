package it.back.product.entity;

import java.time.LocalDateTime;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import it.back.category.entity.CategoryEntity;
import it.back.seller.entity.SellerEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_uid")
    private SellerEntity seller; // seller_uid 컬럼에 해당

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category; // category_id 컬럼에 해당

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "category_id")
    private Integer categoryId; // 직접 매핑할 category_id

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageEntity> productImages;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductDetailEntity productDetail;

}
