package it.back.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
public class ProductDetailEntity {
    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "shipping_info", columnDefinition = "TEXT")
    private String shippingInfo;
}
