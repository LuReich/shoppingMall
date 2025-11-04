package it.back.order.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {

    private Long orderDetailId;
    private Integer categoryId;
    private Long productId;
    private Long sellerUid;
    private Integer quantity;
    private Integer pricePerItem;

    // 추가 정보: 상품명, 썸네일, 판매회사명
    private String productName;
    private String productThumbnailUrl;
    private String companyName;

    private String orderDetailStatus; // Enum 문자열로 처리
    private String orderDetailStatusReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;

}
