package it.back.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDTO {

    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Integer categoryId;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;

    private String description; // 상품 상세 설명 (HTML)
    private String shippingInfo; // 배송/반품 정보
}
