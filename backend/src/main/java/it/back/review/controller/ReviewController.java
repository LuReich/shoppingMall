package it.back.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import it.back.review.service.ReviewService;
import it.back.review.dto.ReviewDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 관리자용 테스트용 미사용 권장 권한 admin 만 있음
    @GetMapping("/product/{productId}")
    public org.springframework.http.ResponseEntity<it.back.common.dto.ApiResponse<java.util.List<ReviewDTO>>> getReviewsByProduct(@PathVariable Long productId) {
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.OK)
            .body(it.back.common.dto.ApiResponse.ok(reviewService.getReviewsByProductId(productId)));
    }
}
