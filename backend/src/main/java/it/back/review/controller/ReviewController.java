package it.back.review.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.dto.ApiResponse;
import it.back.review.dto.ReviewCreateRequest;
import it.back.review.service.ReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 삭제 API (buyer만 가능)
    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Long reviewId, Principal principal) {
        try {
            reviewService.deleteReview(reviewId, principal.getName());
            return ResponseEntity.ok(ApiResponse.ok("리뷰가 삭제되었습니다."));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.forbidden("본인 리뷰만 삭제할 수 있습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        }
    }

    // 리뷰 작성 API
    @PostMapping("write")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createReview(@RequestBody ReviewCreateRequest request, Principal principal) throws Exception {
        Map<String, Object> resultMap = reviewService.createReview(request, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(resultMap));
    }

    // 리뷰 수정 API (buyer만 가능, PATCH)
    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> updateReview(@PathVariable Long reviewId, @RequestBody ReviewCreateRequest request, Principal principal) {
        try {
            reviewService.updateReview(reviewId, request, principal.getName());
            return ResponseEntity.ok(ApiResponse.ok("리뷰가 수정되었습니다."));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.forbidden("본인 리뷰만 수정할 수 있습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        }
    }
}
