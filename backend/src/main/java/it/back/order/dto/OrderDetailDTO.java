package it.back.order.dto;

import java.time.LocalDateTime;

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

    // 추가 정보: 상품명, 썸네일, 판매회사명
    private String productName;
    private String productThumbnailUrl;
    private String companyName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
