package it.back.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @Column(name = "create_at")
    private java.time.LocalDateTime createAt;

    @Column(name = "update_at")
    private java.time.LocalDateTime updateAt;

    public enum OrderDetailStatus {
        PAID, SHIPPING, DELIVERED, CANCELED
    }
}
