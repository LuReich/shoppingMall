package it.back.product.dto;

import lombok.Data;

@Data
public class ProductDetailDTO {

    private Long productId;
    private String description;
    private String shippingInfo;
    private Double averageRating;
    private Integer likeCount;
    private Boolean isDeleted;
    private String deletedByAdminReason;
    private String deletedBySellerReason;
}
