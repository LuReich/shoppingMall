package it.back.product.dto;

import lombok.Data;

@Data
public class ProductDetailDTO {

    private Long productId;
    private String description;
    private String shippingInfo;
}
