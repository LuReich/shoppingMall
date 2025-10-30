package it.back.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpdateDTO {

    // 상품 기본 정보
    private String productName;
    private Integer categoryId;
    private Integer price;
    private Integer stock;

    // 상품 상세 정보
    private String description;
    private String shippingInfo;

    // 이미지 관리 (삭제할 기존 이미지 ID 목록)
    // 이 ID에 해당하는 ProductImageEntity와 실제 파일이 삭제됩니다.
    private List<Long> deleteImageIds;
}
