package it.back.admin.dto;

import it.back.common.pagination.PageRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderSearchDTO extends PageRequestDTO {
    private Long orderId;
    private Long buyerUid;
    private String recipientName;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String orderStatus;
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
}
