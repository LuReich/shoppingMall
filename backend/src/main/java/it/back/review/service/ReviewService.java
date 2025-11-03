package it.back.review.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.order.entity.OrderDetailEntity;
import it.back.order.repository.OrderDetailRepository;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import it.back.review.dto.ReviewCreateRequest;
import it.back.review.dto.ReviewDTO;
import it.back.review.entity.ReviewEntity;
import it.back.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    // 상품별 리뷰 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ReviewEntity> getReviewsByProductIdPaged(Long productId, org.springframework.data.domain.Pageable pageable) {
        return reviewRepository.findAllByProductIdWithBuyerAndSellerPaged(productId, pageable);
    }

    private final ReviewRepository reviewRepository;

    // ProductRepository 주입 (리뷰 작성 시 필요)
    private final ProductRepository productRepository;
    private final BuyerRepository buyerRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public void deleteReview(Long reviewId, String userId) throws IllegalAccessException {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (!review.getBuyer().getBuyerId().equals(userId)) {
            throw new IllegalAccessException("본인 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewCreateRequest request, String userId) throws IllegalAccessException {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        // 본인 리뷰인지 확인
        if (!review.getBuyer().getBuyerId().equals(userId)) {
            throw new IllegalAccessException("본인 리뷰만 수정할 수 있습니다.");
        }
        // orderDetailId가 일치하는지 확인 (구매한 상품에 대한 리뷰만 수정 가능)
        if (request.getOrderDetailId() != null && !review.getOrderDetail().getOrderDetailId().equals(request.getOrderDetailId())) {
            throw new IllegalAccessException("구매한 상품에 대해서만 리뷰를 수정할 수 있습니다.");
        }
        // productId와 orderDetailId 매칭 검증
        if (request.getProductId() != null) {
            Long orderDetailProductId = review.getOrderDetail().getProductId();
            if (!orderDetailProductId.equals(request.getProductId())) {
                throw new IllegalAccessException("orderDetailId와 productId가 일치하지 않습니다.");
            }
        }
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        return reviewRepository.findAllByProductIdWithBuyerAndSeller(productId)
                .stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setReviewId(review.getReviewId());
                    dto.setContent(review.getContent());
                    dto.setRating(review.getRating());
                    dto.setCreateAt(review.getCreateAt());
                    dto.setBuyerNickname(review.getBuyer().getNickname());
                    dto.setCompanyName(review.getProduct().getSeller().getCompanyName());
                    dto.setProductName(review.getProduct().getProductName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 리뷰 작성
    @Transactional
    public Map<String, Object> createReview(ReviewCreateRequest request, String userId) {
        Map<String, Object> resultMap = new HashMap<>();
        // buyer 조회
        BuyerEntity buyer = buyerRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구매자 정보가 없습니다."));
        // 상품 조회
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        // orderDetail 조회
        OrderDetailEntity orderDetail = orderDetailRepository.findById(request.getOrderDetailId())
                .orElseThrow(() -> new IllegalArgumentException("주문 상세 정보가 없습니다."));
        // 구매 인증: orderDetail의 productId, buyerUid 체크
        if (!orderDetail.getProductId().equals(product.getProductId())) {
            throw new IllegalArgumentException("주문 상품과 리뷰 상품이 일치하지 않습니다.");
        }
        if (!orderDetail.getOrder().getBuyerUid().equals(buyer.getBuyerUid())) {
            throw new IllegalArgumentException("해당 상품을 구매한 적이 없습니다.");
        }
        // 중복 리뷰 방지
        List<ReviewEntity> exist = reviewRepository.findByProduct_ProductId(product.getProductId());
        boolean alreadyWritten = exist.stream().anyMatch(r -> r.getOrderDetail().getOrderDetailId().equals(orderDetail.getOrderDetailId()));
        if (alreadyWritten) {
            throw new IllegalArgumentException("이미 리뷰를 작성하셨습니다.");
        }
        // 리뷰 저장
        ReviewEntity review = new ReviewEntity();
        review.setProduct(product);
        review.setBuyer(buyer);
        review.setOrderDetail(orderDetail);
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setCreateAt(LocalDateTime.now());
        reviewRepository.save(review);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "리뷰가 등록되었습니다.");
        resultMap.put("reviewId", review.getReviewId());
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProduct_ProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
    }
}
