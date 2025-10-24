package it.back.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import it.back.review.dto.ReviewDTO;
import it.back.review.entity.ReviewEntity;
import it.back.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId)
                .stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setReviewId(review.getReviewId());
                    dto.setContent(review.getContent());
                    dto.setRating(review.getRating());
                    dto.setCreateAt(review.getCreateAt());
                    dto.setWriter(review.getWriter());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
