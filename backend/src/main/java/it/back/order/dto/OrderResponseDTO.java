package it.back.order.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String buyerPhone; // 주문자 전화번호(주문 시점)
    private String status;
    private Integer totalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;

    // 주문상세(하위) 정보 리스트
    private List<OrderDetailDTO> orderDetails;
}
