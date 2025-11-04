package it.back.product.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.back.product.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "productId", "sellerUid", "categoryId", "productName", "companyName", "price", "stock",
    "thumbnailUrl", "averageRating", "likeCount", "isDeleted", "deletedByAdminReason",
    "deletedBySellerReason", "createAt", "updateAt"
})
public class ProductListDTO {

    private Long productId;
    private Long sellerUid;
    private Integer categoryId;
    private String productName;
    private String companyName;
    private Integer price;
    private Integer stock;
    private String thumbnailUrl;
    private Double averageRating;
    private Integer likeCount;
    private Boolean isDeleted;
    private String deletedByAdminReason;
    private String deletedBySellerReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // ProductEntity를 ProductListDTO로 변환하는 생성자
    public ProductListDTO(ProductEntity product) {
        this.productId = product.getProductId();
        this.sellerUid = product.getSeller() != null ? product.getSeller().getSellerUid() : null;
        this.categoryId = product.getCategoryId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.thumbnailUrl = product.getThumbnailUrl();        
        this.companyName = product.getSeller() != null ? product.getSeller().getCompanyName() : null;
        this.likeCount = product.getLikeCount();
        this.averageRating = product.getAverageRating();
        this.isDeleted = product.getIsDeleted();
        this.deletedByAdminReason = product.getDeletedByAdminReason();
        this.deletedBySellerReason = product.getDeletedBySellerReason();
        this.createAt = product.getCreateAt();
        this.updateAt = product.getUpdateAt();
    }
}
