package it.back.cart.controller;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.back.common.dto.ApiResponse;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<List<CartItemResponseDTO>>> getCartList(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        Long uid = null;
        if (uidObj instanceof Integer) {
            uid = ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            uid = (Long) uidObj;
        }
        if (uid == null)
            throw new IllegalArgumentException("uid is null");
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(cartService.getCartList(uid)));
    }

    // 로그인한 buyer 상품 카트 추가용
    @PostMapping("/add")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, String>>> addToCart(@RequestBody CartDTO dto,
            Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        Long uid = null;
        if (uidObj instanceof Integer) {
            uid = ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            uid = (Long) uidObj;
        }
        if (uid == null)
            throw new IllegalArgumentException("uid is null");
        String nickname = (String) details.get("nickname");
        Map<String, String> response = new HashMap<>();
        String adjustMsg = null;
        try {
            cartService.addToCart(uid, dto.getProductId(), dto.getQuantity());
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            adjustMsg = e.getMessage();
        }
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : "";
        String msg = (adjustMsg == null)
                ? String.format("%s 님의 상품 장바구니 추가가 완료되었습니다.", who)
                : String.format("%s 님의 상품 장바구니 추가가 완료되었습니다. (%s)", who, adjustMsg);
        System.out.println(msg); // 서버 로그
        response.put("msg", msg);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    // buyer의 카트 안에 있는 상품 수량 변경 용
    @PatchMapping("/{cartId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateCartQuantity(
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> body,
            Authentication authentication) {

        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        Long uid = null;
        if (uidObj instanceof Integer) {
            uid = ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            uid = (Long) uidObj;
        }
        if (uid == null)
            throw new IllegalArgumentException("uid is null");
        String nickname = (String) details.get("nickname");
        Map<String, String> response = new HashMap<>();
        Integer quantity = body.get("quantity");
        try {
            cartService.updateCartQuantity(cartId, quantity, uid);
            String who = (nickname != null && !nickname.isEmpty()) ? nickname : "";
            String msg = String.format("%s 님의 장바구니 수량이 %d개로 변경되었습니다.", who, quantity);
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
        } catch (IllegalStateException e) {
            String who = (nickname != null && !nickname.isEmpty()) ? nickname : "";
            String msg = String.format("%s 님의 장바구니 수량이 최대 재고로 자동 조정되었습니다. (%s)", who, e.getMessage());
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.ok(response));
        }
    }

    // 개별 카트 삭제용
    @DeleteMapping("/{cartId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, String>>> removeFromCart(@PathVariable Long cartId,
            Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        Long uid = null;
        if (uidObj instanceof Integer) {
            uid = ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            uid = (Long) uidObj;
        }
        if (uid == null)
            throw new IllegalArgumentException("uid is null");
        String nickname = (String) details.get("nickname");
        cartService.removeFromCart(cartId, uid);
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : "";
        String msg = String.format("%s 님의 %d번 장바구니 삭제가 완료되었습니다.", who, cartId);
        System.out.println(msg); // 서버 로그
        Map<String, String> response = new HashMap<>();
        response.put("msg", msg);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    // 체크박스 선택된 카트 삭제용인데 잘 써질지를 모르겠네요
    @DeleteMapping("/selected")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, String>>> removeFromCartAtOnce(@RequestBody List<Long> cartIds,
            Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        Long uid = null;
        if (uidObj instanceof Integer) {
            uid = ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            uid = (Long) uidObj;
        }
        if (uid == null)
            throw new IllegalArgumentException("uid is null");
        String nickname = (String) details.get("nickname");
        cartService.removeFromCartAtOnce(cartIds, uid);
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : "";
        String msg = String.format("%s 님의 %d개 장바구니 삭제가 완료되었습니다.", who, cartIds.size());
        System.out.println(msg); // 서버 로그
        Map<String, String> response = new HashMap<>();
        response.put("msg", msg);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }
}
