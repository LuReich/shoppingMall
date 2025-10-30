package it.back.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private Long reviewId;
    private Long buyerUid;
    private Long orderDetailId;
    private Long productId;
    private Long sellerUid;
    private String buyerNickname;
    private int rating;
    private String content;
    private String productName;
    private String companyName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
