package it.back.product.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ProductDTO {

    private Long productId;
    private Long sellerUid;
    private Integer categoryId;
    private String productName;
    private Integer price;
    private Integer stock;
    private String thumbnailUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonProperty("isDeleted")
    private Boolean isDeleted;

}
