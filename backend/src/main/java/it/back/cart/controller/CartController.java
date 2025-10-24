package it.back.cart.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public ResponseEntity<List<CartItemResponseDTO>> getCartList(Authentication authentication) {
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long uid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue() : (Long) details.get("uid");
        return ResponseEntity.ok(cartService.getCartList(uid));
    }

    // 로그인한 buyer 상품 카트 추가용
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartDTO dto, Authentication authentication) {
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long uid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue() : (Long) details.get("uid");
        String nickname = (String) details.get("nickname");
        Map<String, String> response = new HashMap<>();
        String adjustMsg = null;
        try {
            cartService.addToCart(uid, dto.getProductId(), dto.getQuantity());
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            adjustMsg = e.getMessage();
        }
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : loginId;
        String msg = (adjustMsg == null)
                ? String.format("%s 님의 상품 장바구니 추가가 완료되었습니다.", who)
                : String.format("%s 님의 상품 장바구니 추가가 완료되었습니다. (%s)", who, adjustMsg);
        System.out.println(msg); // 서버 로그
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    // buyer의 카트 안에 있는 상품 수량 변경 용
    @PatchMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> updateCartQuantity(
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> body,
            Authentication authentication
    ) {
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long uid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue() : (Long) details.get("uid");
        String nickname = (String) details.get("nickname");
        Map<String, String> response = new HashMap<>();
        Integer quantity = body.get("quantity");
        try {
            cartService.updateCartQuantity(cartId, quantity, uid);
            String who = (nickname != null && !nickname.isEmpty()) ? nickname : loginId;
            String msg = String.format("%s 님의 장바구니 수량이 %d개로 변경되었습니다.", who, quantity);
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            String who = (nickname != null && !nickname.isEmpty()) ? nickname : loginId;
            String msg = String.format("%s 님의 장바구니 수량이 최대 재고로 자동 조정되었습니다. (%s)", who, e.getMessage());
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 개별 카트 삭제용
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> removeFromCart(@PathVariable Long cartId, Authentication authentication) {
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long uid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue() : (Long) details.get("uid");
        String nickname = (String) details.get("nickname");
        cartService.removeFromCart(cartId, uid);
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : loginId;
        String msg = String.format("%s 님의 %d번 장바구니 삭제가 완료되었습니다.", who, cartId);
        System.out.println(msg); // 서버 로그
        Map<String, String> response = new HashMap<>();
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    // 체크박스 선택된 카트 삭제용인데 잘 써질지를 모르겠네요
    @DeleteMapping("/selected")
    public ResponseEntity<Map<String, String>> removeFromCartAtOnce(@RequestBody List<Long> cartIds, Authentication authentication) {
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long uid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue() : (Long) details.get("uid");
        String nickname = (String) details.get("nickname");
        cartService.removeFromCartAtOnce(cartIds, uid);
        String who = (nickname != null && !nickname.isEmpty()) ? nickname : loginId;
        String msg = String.format("%s 님의 %d개 장바구니 삭제가 완료되었습니다.", who, cartIds.size());
        System.out.println(msg); // 서버 로그
        Map<String, String> response = new HashMap<>();
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }
}
