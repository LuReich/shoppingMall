package it.back.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long orderId;
    private Long buyerUid;
    private Integer totalPrice;
    private String recipientName;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String orderStatus; // Enum 문자열로 처리
    private List<OrderDetailDTO> orderDetails;

    // 정적 팩토리 메서드: Entity → DTO
    public static OrderDTO from(it.back.order.entity.OrderEntity order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setBuyerUid(order.getBuyerUid());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientAddress(order.getRecipientAddress());
        dto.setRecipientAddressDetail(order.getRecipientAddressDetail());
        dto.setOrderStatus(order.getOrderStatus().name());
        if (order.getOrderDetails() != null) {
            dto.setOrderDetails(order.getOrderDetails().stream()
                .map(OrderDetailDTO::from)
                .toList());
        }
        return dto;
    }
}
