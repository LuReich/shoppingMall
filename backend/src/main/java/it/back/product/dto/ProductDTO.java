package it.back.product.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ProductDTO {

    private Long productId;
    private Long sellerUid;
    private Integer categoryId;
    private String productName;
    private String companyName;
    private Integer price;
    private Integer stock;
    private String thumbnailUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Double averageRating;
    private Integer likeCount;
    private List<ProductImageDTO> productImages; // 상품 이미지 리스트 추가

    // ProductEntity를 ProductDTO로 변환하는 생성자
    public ProductDTO(ProductEntity product) {
        this.productId = product.getProductId();
        this.sellerUid = product.getSeller() != null ? product.getSeller().getSellerUid() : null;
        this.categoryId = product.getCategoryId(); // 직접 매핑된 categoryId 사용
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.thumbnailUrl = product.getThumbnailUrl();
        this.createAt = product.getCreateAt();
        this.updateAt = product.getUpdateAt();
        this.isDeleted = product.getIsDeleted();
        this.companyName = product.getSeller() != null ? product.getSeller().getCompanyName() : null;
        this.likeCount = product.getLikeCount();
        this.averageRating = product.getAverageRating();
        // ProductImageEntity 리스트를 ProductImageDTO 리스트로 변환하여 설정
        if (product.getProductImages() != null) {
            this.productImages = product.getProductImages().stream()
                                    .map(ProductImageDTO::new)
                                    .collect(Collectors.toList());
        }
    }
}
