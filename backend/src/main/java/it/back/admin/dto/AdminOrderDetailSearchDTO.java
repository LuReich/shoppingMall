package it.back.admin.dto;

import it.back.common.pagination.PageRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderDetailSearchDTO extends PageRequestDTO {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private Long sellerUid;
    private String orderDetailStatus;
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
}
