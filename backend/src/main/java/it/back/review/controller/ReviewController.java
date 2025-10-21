package it.back.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import it.back.review.service.ReviewService;
import it.back.review.dto.ReviewDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewDTO> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId)
                .stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setReviewId(review.getReviewId());
                    dto.setContent(review.getContent());
                    dto.setRating(review.getRating());
                    dto.setCreatedAt(review.getCreatedAt());
                    dto.setWriter(review.getWriter());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}