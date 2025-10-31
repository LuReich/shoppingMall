package it.back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import it.back.product.entity.ProductImageEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

    private Long imageId;
    private Long productId;
    private String imageName;
    private String storedName;
    private String imagePath;
    private Long imageSize;
    private Integer sortOrder;

    // ProductImageEntity를 받아 ProductImageDTO를 생성하는 생성자 추가
    public ProductImageDTO(ProductImageEntity entity) {
        this.imageId = entity.getImageId();
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.imageName = entity.getImageName();
        this.storedName = entity.getStoredName();
        this.imagePath = entity.getImagePath();
        this.imageSize = entity.getImageSize();
        this.sortOrder = entity.getSortOrder();
    }

    // 필요에 따라 Entity -> DTO 변환 생성자 또는 메서드를 추가할 수 있습니다.
    // public ProductImageDTO(ProductImageEntity entity) { ... }
}
