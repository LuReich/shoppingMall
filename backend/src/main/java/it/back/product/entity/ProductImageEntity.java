package it.back.product.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "image_name", nullable = false, length = 255)
    private String imageName; // 원본 파일명

    @Column(name = "stored_name", nullable = false, unique = true, length = 255)
    private String storedName; // 서버에 저장된 파일명 (UUID)

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath; // 이미지 접근 가능 경로

    @Column(name = "image_size")
    private Long imageSize; // 파일 크기 (bytes)

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // 연관관계 편의 메서드
    public void setProduct(ProductEntity product) {
        this.product = product;
        if (product != null && !product.getProductImages().contains(this)) {
            product.getProductImages().add(this);
        }
    }
}
