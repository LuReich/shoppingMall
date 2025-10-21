package it.back.product.dto;

import lombok.Data;
import java.util.List;

import it.back.review.dto.ReviewDTO;

@Data
public class ProductDetailDTO {
	private Long productId;
	private String description;
	private String shippingInfo;
	private List<ReviewDTO> reviews;

	public List<ReviewDTO> getReviews() {
		return reviews;
	}
	public void setReviews(List<ReviewDTO> reviews) {
		this.reviews = reviews;
	}
}



