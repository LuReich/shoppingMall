package it.back.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import it.back.order.entity.OrderEntity;

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

}
