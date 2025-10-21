package it.back.product.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
	private Long productId;
	private Long sellerUid;
	private Integer categoryId;
	private Integer price;
	private Integer stock;
	private String thumbnailUrl;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
	private Boolean isDeleted;
}



