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

    // 관리자용 테스트용 미사용 권장 권한 admin 만 있음
    @GetMapping("/product/{productId}")
    public List<ReviewDTO> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }
}
