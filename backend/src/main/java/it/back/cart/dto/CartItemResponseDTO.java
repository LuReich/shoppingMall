package it.back.cart.dto;

import java.time.LocalDateTime;

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
    private String sellerCompanyName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 장바구니 최대치 안내 메시지
    private String message;
}
