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

/**
 * 상품 수정 화면에서 기존 상품의 모든 정보를 불러올 때 사용하는 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateResponseDTO {

    // 상품 기본 정보
    private Long productId;
    private Long sellerUid;
    private String companyName;
    private Integer categoryId;
    private String productName;
    private Integer price;
    private Integer stock;
    private String thumbnailUrl;

    // 이미지 정보
    private List<ProductImageDTO> productImages; // 서브 이미지 리스트

    // 상품 상세 정보
    private String description;
    private String shippingInfo;

    // 메타 정보
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonProperty("isDeleted")
    private Boolean isDeleted;    // ProductEntity를 ProductUpdateResponseDTO로 변환하는 생성자

    public ProductUpdateResponseDTO(ProductEntity product) {
        this.productId = product.getProductId();
        this.sellerUid = product.getSeller() != null ? product.getSeller().getSellerUid() : null;
        this.companyName = product.getSeller() != null ? product.getSeller().getCompanyName() : null;
        this.categoryId = product.getCategoryId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.thumbnailUrl = product.getThumbnailUrl();
        this.createAt = product.getCreateAt();
        this.updateAt = product.getUpdateAt();
        this.isDeleted = product.getIsDeleted();

        // 상품 상세 정보
        if (product.getProductDetail() != null) {
            this.description = product.getProductDetail().getDescription();
            this.shippingInfo = product.getProductDetail().getShippingInfo();
        }

        // 서브 이미지 리스트
        if (product.getProductImages() != null) {
            this.productImages = product.getProductImages().stream()
                    .map(ProductImageDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
