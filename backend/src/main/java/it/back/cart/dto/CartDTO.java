package it.back.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
    private Long cartId;
    private Long buyerUid;
    private Long productId;
    private Integer quantity;
}