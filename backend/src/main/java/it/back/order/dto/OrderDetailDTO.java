package it.back.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private Long orderDetailId;
    private Long productId;
    private Long sellerUid;
    private Integer quantity;
    private Integer pricePerItem;
    private String orderDetailStatus; // Enum 문자열로 처리

    // 정적 팩토리 메서드: Entity → DTO
    public static OrderDetailDTO from(it.back.order.entity.OrderDetailEntity detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailId(detail.getOrderDetailId());
        dto.setProductId(detail.getProductId());
        dto.setSellerUid(detail.getSellerUid());
        dto.setQuantity(detail.getQuantity());
        dto.setPricePerItem(detail.getPricePerItem());
        dto.setOrderDetailStatus(detail.getOrderDetailStatus().name());
        return dto;
    }
}
