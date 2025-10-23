package it.back.cart.controller;

import it.back.cart.dto.CartDTO;
import it.back.cart.dto.CartItemResponseDTO;
import it.back.cart.service.CartService;
import it.back.admin.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public ResponseEntity<List<CartItemResponseDTO>> getCartList(Authentication authentication) {
        UserSummaryDTO user = (UserSummaryDTO) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.getCartList(user.getUid()));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartDTO dto, Authentication authentication) {
        UserSummaryDTO user = (UserSummaryDTO) authentication.getPrincipal();
        Map<String, String> response = new HashMap<>();
        String adjustMsg = null;
        try {
            cartService.addToCart(user.getUid(), dto.getProductId(), dto.getQuantity());
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            adjustMsg = e.getMessage();
        }
        String who = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : user.getLoginId();
        String msg = (adjustMsg == null)
            ? String.format("%s 님의 상품 장바구니 추가가 완료되었습니다.", who)
            : String.format("%s 님의 상품 장바구니 추가가 완료되었습니다. (%s)", who, adjustMsg);
        System.out.println(msg); // 서버 로그
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> updateCartQuantity(
        @PathVariable Long cartId,
        @RequestBody Map<String, Integer> body,
        Authentication authentication
    ) {
        UserSummaryDTO user = (UserSummaryDTO) authentication.getPrincipal();
        Map<String, String> response = new HashMap<>();
        Integer quantity = body.get("quantity");
        try {
            cartService.updateCartQuantity(cartId, quantity, user.getUid());
            String who = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : user.getLoginId();
            String msg = String.format("%s 님의 장바구니 수량이 %d개로 변경되었습니다.", who, quantity);
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            String who = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : user.getLoginId();
            String msg = String.format("%s 님의 장바구니 수량이 최대 재고로 자동 조정되었습니다. (%s)", who, e.getMessage());
            System.out.println(msg);
            response.put("msg", msg);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> removeFromCart(@PathVariable Long cartId, Authentication authentication) {
    UserSummaryDTO user = (UserSummaryDTO) authentication.getPrincipal();
    cartService.removeFromCart(cartId, user.getUid());
    String who = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : user.getLoginId();
    String msg = String.format("%s 님의 %d번 장바구니 삭제가 완료되었습니다.", who, cartId);
    System.out.println(msg); // 서버 로그
    Map<String, String> response = new HashMap<>();
    response.put("msg", msg);
    return ResponseEntity.ok(response);
    }

    @DeleteMapping("/selected")
    public ResponseEntity<Map<String, String>> removeFromCartAtOnce(@RequestBody List<Long> cartIds, Authentication authentication) {
    UserSummaryDTO user = (UserSummaryDTO) authentication.getPrincipal();
    cartService.removeFromCartAtOnce(cartIds, user.getUid());
    String who = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : user.getLoginId();
    String msg = String.format("%s 님의 %d개 장바구니 삭제가 완료되었습니다.", who, cartIds.size());
    System.out.println(msg); // 서버 로그
    Map<String, String> response = new HashMap<>();
    response.put("msg", msg);
    return ResponseEntity.ok(response);
    }
}
