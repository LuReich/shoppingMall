package it.back.cart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.cart.dto.CartDTO;
import it.back.cart.dto.CartItemResponseDTO;
import it.back.cart.service.CartService;
import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private long getBuyerUid(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        if (uidObj == null) {
            throw new IllegalStateException("사용자 UID를 찾을 수 없습니다.");
        }
        return ((Number) uidObj).longValue();
    }

    // 장바구니 목록 조회 (페이징 처리)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<CartItemResponseDTO>>> getCartList(
            Authentication authentication,
            Pageable pageable) {
        long buyerId = getBuyerUid(authentication);
        PageResponseDTO<CartItemResponseDTO> result = cartService.getCartList(buyerId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 장바구니 상품 추가
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemResponseDTO>> addToCart(
            Authentication authentication,
            @RequestBody CartDTO cartDTO) {
        long buyerId = getBuyerUid(authentication);
        CartItemResponseDTO responseDTO = cartService.addCartItem(buyerId, cartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(responseDTO));
    }

    // 장바구니 상품 수량 변경
    @PatchMapping("/{cartId}")
    public ResponseEntity<ApiResponse<CartItemResponseDTO>> updateCartQuantity(
            Authentication authentication,
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> payload) {
        long buyerId = getBuyerUid(authentication);
        int quantity = payload.get("quantity");
        CartItemResponseDTO responseDTO = cartService.updateQuantity(cartId, quantity, buyerId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    // 장바구니 상품 개별 삭제
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            Authentication authentication,
            @PathVariable Long cartId) {
        long buyerId = getBuyerUid(authentication);
        String message = cartService.deleteCartItem(cartId, buyerId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }

    // 선택된 장바구니 상품 삭제
    @DeleteMapping("/selected")
    public ResponseEntity<ApiResponse<String>> removeSelectedCartItems(
            Authentication authentication,
            @RequestBody List<Long> cartIds) {
        long buyerId = getBuyerUid(authentication);
        String message = cartService.deleteSelectedCartItems(cartIds, buyerId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }
}
