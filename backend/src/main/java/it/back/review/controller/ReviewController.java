package it.back.review.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import it.back.review.service.ReviewService;
import it.back.common.dto.ApiResponse;
import it.back.review.dto.ReviewDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 관리자용 테스트용 미사용 권장 권한 admin 만 있음
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(reviewService.getReviewsByProductId(productId)));
    }
}
