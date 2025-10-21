package it.back.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import it.back.review.entity.ReviewEntity;
import it.back.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<ReviewEntity> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId);
    }
}