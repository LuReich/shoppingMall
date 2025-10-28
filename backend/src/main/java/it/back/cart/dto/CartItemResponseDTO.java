package it.back.cart.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponseDTO {

    private Long cartId;
    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private Integer quantity;
    private Integer pricePerItem;
    private Long sellerUid; // 판매자 식별자 추가
    private String sellerCompanyName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // 장바구니 최대치 안내 메시지
    private String message;
}
