package it.back.product.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequestDTO {

    // 상품 기본 정보
    private String productName;
    private Integer categoryId;
    private Integer price;
    private Integer stock;

    // 상품 상세 정보
    private String description;
    private String shippingInfo;

    // imageMapping: React Quill에서 data-image-id 순서대로 이미지 ID 배열 (새 이미지만)
    private List<String> imageMapping;

    // 이미지 관리 (삭제할 기존 서브 이미지 ID 목록)
    private List<Long> deleteImageIds;

    // 삭제할 기존 메인 이미지 URL
    private String deleteMainImage;
}
