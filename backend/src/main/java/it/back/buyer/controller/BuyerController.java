package it.back.buyer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.buyer.dto.BuyerRegisterDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.ApiResponse;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.order.dto.OrderResponseDTO;
import it.back.order.service.OrderService;
import it.back.product.dto.ProductListDTO;
import it.back.product.service.ProductLikeService;
import it.back.review.dto.ReviewDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;
    private final BuyerRepository buyerRepository;
    private final OrderService orderService;
    private final ProductLikeService productLikeService;

    // 로그인한 buyer 가 자기 정보 보는 용도
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> getMyInfo(Authentication authentication) {
        String loginId = authentication.getName();
        System.out.println("[BuyerController] /me loginId: " + loginId);
        BuyerEntity buyer = buyerRepository.findByBuyerId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 buyerId 없음: " + loginId));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(new BuyerResponseDTO(buyer)));
    }

    // buyer 전용 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequestDTO dto) {
        String jwt = buyerService.login(dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseBody));
    }

    // buyer 자기 정보 수정
    @PatchMapping("/{buyerUid}")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> updateBuyer(
            @PathVariable("buyerUid") Long buyerUid,
            @Valid @RequestBody BuyerUpdateRequestDTO request,
            Authentication authentication) {

        BuyerEntity updatedEntity = buyerService.updateBuyer(buyerUid, request, authentication);
        BuyerResponseDTO updatedBuyer = new BuyerResponseDTO(updatedEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedBuyer));
    }

    // 이메일 중복 및 형식 체크
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestBody Map<String, Object> body,
            Authentication authentication) {
        String email = (String) body.get("buyerEmail");
        Long buyerUid = parseUidFromBody(body, "buyerUid");
        if (buyerUid == null) {
            buyerUid = extractUidFromAuth(authentication);
        }
        try {
            boolean isSameAsSelf = buyerService.checkEmail(email, buyerUid);
            String message = isSameAsSelf ? "이전과 동일한 이메일입니다." : "사용 가능한 이메일입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 전화번호 중복 및 형식 체크
    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse<String>> checkPhone(@RequestBody Map<String, Object> body,
            Authentication authentication) {
        String phone = (String) body.get("phone");
        Long buyerUid = parseUidFromBody(body, "buyerUid");
        if (buyerUid == null) {
            buyerUid = extractUidFromAuth(authentication);
        }
        try {
            boolean isSameAsSelf = buyerService.checkPhone(phone, buyerUid);
            String message = isSameAsSelf ? "이전과 동일한 전화번호입니다." : "사용 가능한 전화번호입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 아이디 중복 체크
    @PostMapping("/check-buyerId")
    public ResponseEntity<ApiResponse<String>> checkBuyerId(@RequestBody Map<String, Object> body,
            Authentication authentication) {
        String buyerId = (String) body.get("buyerId");
        Long buyerUid = parseUidFromBody(body, "buyerUid");
        if (buyerUid == null) {
            buyerUid = extractUidFromAuth(authentication);
        }
        try {
            boolean isSameAsSelf = buyerService.checkBuyerId(buyerId, buyerUid);
            String message = isSameAsSelf ? "이전과 동일한 아이디입니다." : "사용 가능한 아이디입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }
    // ...매핑 메서드들...

    // buyer 회원가입 용
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> registerBuyer(@Valid @RequestBody BuyerRegisterDTO buyerRegisterDto) {
        BuyerResponseDTO result = buyerService.registerBuyer(buyerRegisterDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // buyer 본인 탈퇴 (isActive=0)
    @PatchMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> buyerWithdraw(@RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        String loginId = authentication.getName();
        String withdrawalReason = (body != null) ? body.getOrDefault("withdrawalReason", "") : "";
        buyerService.buyerWithdraw(loginId, withdrawalReason);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("회원 탈퇴(비활성화) 처리되었습니다."));
    }

    // buyer 주문 이력 조회 (페이지네이션)
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderResponseDTO>>> getMyOrders(
            Authentication authentication,
            Pageable pageable) {
        Long buyerUid = extractUidFromAuth(authentication);
        if (buyerUid == null) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }

        PageResponseDTO<OrderResponseDTO> result = orderService.getOrdersByBuyerUid(buyerUid, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 로그인한 buyer 가 자기가 쓴 리뷰 목록을 보는 용도 (페이지네이션, 상품명 검색 포함)
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PageResponseDTO<ReviewDTO>>> getMyReviews(
            Authentication authentication,
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "productName", required = false) String productName) {

        Long buyerUid = extractUidFromAuth(authentication);
        if (buyerUid == null) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }

        PageResponseDTO<ReviewDTO> result = buyerService.getMyReviews(buyerUid, pageRequestDTO, productName);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 상품 좋아요
    @PostMapping("/like/{productId}")
    public ResponseEntity<ApiResponse<String>> toggleLike(Authentication authentication, @PathVariable("productId") Long productId) {
        Long buyerUid = extractUidFromAuth(authentication);
        if (buyerUid == null) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }
        boolean liked = productLikeService.toggleLike(buyerUid, productId);
        String message = liked ? "상품을 좋아합니다." : "상품 좋아요를 취소합니다.";
        return ResponseEntity.ok(ApiResponse.ok(message));
    }

    // buyer 좋아요 한 상품 조회, 상품의 좋아요 많은 순, 평점 높은 순 정렬
    @GetMapping("/like/product/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductListDTO>>> getLikedProducts(
            Authentication authentication,
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(name = "productId", required = false) Long productId) {
        Long buyerUid = extractUidFromAuth(authentication);
        if (buyerUid == null) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }

        PageResponseDTO<ProductListDTO> result = buyerService.getLikedProducts(buyerUid, pageRequestDTO, productName, companyName, productId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ====== 유틸리티 메서드 ======
    // Map에서 Long uid 추출 공통 메서드
    private static Long parseUidFromBody(Map<String, Object> body, String key) {
        Object uidObj = body.get(key);
        if (uidObj instanceof Integer i) {
            return i.longValue();
        }
        if (uidObj instanceof Long l) {
            return l;
        }
        if (uidObj instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    // JWT에서 uid 추출 공통 메서드
    private static Long extractUidFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object detailsObj = authentication.getDetails();
        if (detailsObj instanceof Map<?, ?> details) {
            Object uidObj = details.get("uid");
            if (uidObj instanceof Integer i) {
                return i.longValue();
            }
            if (uidObj instanceof Long l) {
                return l;
            }
            if (uidObj instanceof String s) {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return null;
    }

}
