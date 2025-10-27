package it.back.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private OrderEntity order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "seller_uid", nullable = false)
    private Long sellerUid;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_per_item", nullable = false)
    private Integer pricePerItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_detail_status", nullable = false)
    private OrderDetailStatus orderDetailStatus = OrderDetailStatus.PAID;


    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    public enum OrderDetailStatus {
        PAID, SHIPPING, DELIVERED, CANCELED
    }
}
