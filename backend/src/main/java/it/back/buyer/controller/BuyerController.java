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
import org.springframework.web.bind.annotation.RestController;

import it.back.buyer.dto.BuyerRegisterDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.ApiResponse;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.order.dto.OrderResponseDTO;
import it.back.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;
    private final BuyerRepository buyerRepository;
    private final OrderService orderService;

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
            @PathVariable Long buyerUid,
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
        // 인증 정보에서 buyerUid 추출 (OrderController 참고)
        Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> details)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }
        Object roleObj = details.get("role");
        if (roleObj == null || !"BUYER".equals(roleObj.toString())) {
            throw new IllegalStateException("구매자(BUYER) 권한이 필요합니다.");
        }
        Object uidObj = details.get("uid");
        Long buyerUid;
        if (uidObj instanceof Integer i) {
            buyerUid = i.longValue();
        } else if (uidObj instanceof Long l) {
            buyerUid = l;
        } else {
            throw new IllegalStateException("uid 타입이 올바르지 않습니다.");
        }
        // OrderService에서 주문 이력 조회
        PageResponseDTO<OrderResponseDTO> result = orderService.getOrdersByBuyerUid(buyerUid, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // ====== 유틸리티 메서드 ======
    // Map에서 Long uid 추출 공통 메서드
    private static Long parseUidFromBody(Map<String, Object> body, String key) {
        Object uidObj = body.get(key);
        if (uidObj instanceof Integer i) return i.longValue();
        if (uidObj instanceof Long l) return l;
        if (uidObj instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignore) {}
        }
        return null;
    }

    // JWT에서 uid 추출 공통 메서드
    private static Long extractUidFromAuth(Authentication authentication) {
        if (authentication == null) return null;
        Object detailsObj = authentication.getDetails();
        if (detailsObj instanceof Map<?, ?> details) {
            Object uidObj = details.get("uid");
            if (uidObj instanceof Integer i) return i.longValue();
            if (uidObj instanceof Long l) return l;
            if (uidObj instanceof String s) {
                try { return Long.parseLong(s); } catch (NumberFormatException ignore) {}
            }
        }
        return null;
    }


}
