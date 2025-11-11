package it.back.admin.dto;

import it.back.order.entity.OrderEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderResponseDTO {
    private Long orderId;
    private Long buyerUid;
    private String buyerPhone;
    private String recipientName;
    private String recipientAddress;
    private String recipientAddressDetail;
    private Integer totalPrice;
    private String orderStatus;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<AdminOrderDetailResponseDTO> orderDetail;

    public static AdminOrderResponseDTO fromEntity(OrderEntity entity) {
        List<AdminOrderDetailResponseDTO> detailDTOs = entity.getOrderDetails().stream()
                .map(AdminOrderDetailResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return AdminOrderResponseDTO.builder()
                .orderId(entity.getOrderId())
                .buyerUid(entity.getBuyerUid())
                .buyerPhone(entity.getBuyerPhone())
                .recipientName(entity.getRecipientName())
                .recipientAddress(entity.getRecipientAddress())
                .recipientAddressDetail(entity.getRecipientAddressDetail())
                .totalPrice(entity.getTotalPrice())
                .orderStatus(entity.getOrderStatus().name())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .orderDetail(detailDTOs)
                .build();
    }
}
