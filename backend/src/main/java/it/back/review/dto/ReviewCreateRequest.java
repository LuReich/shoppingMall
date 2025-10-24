package it.back.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {

    private Long productId;
    private int rating;
    private String content;
    private Long orderDetailId; // 필요시 orderDetailId, buyerUid 등 추가 가능
}
