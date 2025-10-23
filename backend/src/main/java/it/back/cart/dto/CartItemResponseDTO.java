package it.back.cart.dto;

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
}
