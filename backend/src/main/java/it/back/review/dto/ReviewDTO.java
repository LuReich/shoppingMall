package it.back.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private Long buyerUid;
    private Long sellerUid;
    private Long productId;
    private Long orderDetailId;
    private LocalDateTime updateAt;

    private Long reviewId;
    private String content;
    private int rating;
    private LocalDateTime createAt;
    private String buyerNickname;
    private String sellerCompanyName;
    private String productName; // 상품명
}
