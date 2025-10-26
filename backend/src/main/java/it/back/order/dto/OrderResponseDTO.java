package it.back.order.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

    // 주문(상위) 정보
    private Long orderId;
    private String recipientName;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String recipientPhoneNumber; // 필요시
    private String status;
    private Integer totalPrice;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // 주문상세(하위) 정보 리스트
    private List<OrderDetailDTO> orderDetails;
}
