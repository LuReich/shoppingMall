package it.back.admin.dto;

import it.back.order.entity.OrderDetailEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDetailResponseDTO {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private String productName;
    private Long sellerUid;
    private Integer quantity;
    private Integer pricePerItem;
    private String orderDetailStatus;
    private String orderDetailStatusReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static AdminOrderDetailResponseDTO fromEntity(OrderDetailEntity entity) {
        return AdminOrderDetailResponseDTO.builder()
                .orderDetailId(entity.getOrderDetailId())
                .orderId(entity.getOrder().getOrderId())
                .productId(entity.getProductId())
                .productName(entity.getProduct() != null ? entity.getProduct().getProductName() : "Product Name Not Available")
                .sellerUid(entity.getSellerUid())
                .quantity(entity.getQuantity())
                .pricePerItem(entity.getPricePerItem())
                .orderDetailStatus(entity.getOrderDetailStatus().name())
                .orderDetailStatusReason(entity.getOrderDetailStatusReason())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
