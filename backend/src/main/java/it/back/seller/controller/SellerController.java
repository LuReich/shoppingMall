package it.back.seller.controller;
import java.util.HashMap;
import java.util.Map;

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

import it.back.common.dto.ApiResponse;
import it.back.common.dto.LoginRequestDTO;
import it.back.seller.dto.SellerPublicDTO;
import it.back.seller.dto.SellerRegisterDTO;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.dto.SellerUpdateRequestDTO;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerRepository sellerRepository;

    
    // 로그인한 seller 가 자기 정보 불러오기
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> getMyInfo(Authentication authentication) {
        String loginId = authentication.getName();
        System.out.println("[SellerController] /me loginId: " + loginId);
        SellerEntity seller = sellerRepository.findBySellerId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 sellerId 없음: " + loginId));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(new SellerResponseDTO(seller)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequestDTO dto) {
        String jwt = sellerService.login(dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseBody));
    }

    /*
     * // [form-data 방식으로 바꾸고 싶을 때 참고]
     * 
     * // 또는
     * // public ResponseEntity<?> login(@ModelAttribute LoginRequestDTO dto) { ...
     * }
     */
    // @PostMapping("/login")
    // public ResponseEntity<Map<String, String>> login(@RequestParam String
    // loginId, @RequestParam String password) {
    // LoginRequestDTO dto = new LoginRequestDTO();
    // dto.setLoginId(loginId);
    // dto.setPassword(password);
    // String jwt = sellerService.login(dto);
    // Map<String, String> responseBody = new HashMap<>();
    // responseBody.put("message", "Login successful");
    // responseBody.put("token", "bearer: " + jwt);
    // return ResponseEntity.ok().body(responseBody);
    // }
    // 공개용 판매자 정보 조회 (비로그인/로그인 모두 접근 가능)
    @GetMapping("/public/{sellerUid}")
    public ResponseEntity<ApiResponse<SellerPublicDTO>> getSellerPublicInfo(@PathVariable Long sellerUid) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(sellerService.getSellerPublicInfo(sellerUid)));
    }

    // seller 등록용
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> registerSeller(@Valid @RequestBody SellerRegisterDTO sellerRegisterDto) {
        SellerResponseDTO result = sellerService.registerSeller(sellerRegisterDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 아이디 중복 체크
    @PostMapping("/check-sellerId")
    public ResponseEntity<ApiResponse<String>> checkSellerId(@RequestBody Map<String, Object> body,
            Authentication authentication) {

        String sellerId = (String) body.get("sellerId");
        Long sellerUid = getUidFromRequest(body, "sellerUid");

        // If UID not in body (self-update or registration), get it from auth token
        if (sellerUid == null && authentication != null) {
            String loginId = authentication.getName();
            sellerUid = sellerRepository.findBySellerId(loginId)
                                        .map(SellerEntity::getSellerUid)
                                        .orElse(null);
        }

        try {
            boolean isSameAsSelf = sellerService.checkSellerId(sellerId, sellerUid);
            String message = isSameAsSelf ? "이전과 동일한 아이디입니다." : "사용 가능한 아이디입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 판매자 자기 정보 수정 (PATCH)
    @PatchMapping("/{sellerUid}")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> updateSeller(
            @PathVariable Long sellerUid,
            @Valid @RequestBody SellerUpdateRequestDTO request,
            Authentication authentication) {
        SellerResponseDTO updated = sellerService.updateSeller(sellerUid, request, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updated));
    }


    // 이메일 중복/형식 체크
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestBody Map<String, Object> body,
            Authentication authentication) {
        String email = (String) body.get("sellerEmail");
        Long sellerUid = getUidFromRequest(body, "sellerUid");

        if (sellerUid == null && authentication != null) {
            String loginId = authentication.getName();
            sellerUid = sellerRepository.findBySellerId(loginId)
                                        .map(SellerEntity::getSellerUid)
                                        .orElse(null);
        }

        try {
            boolean isSameAsSelf = sellerService.checkEmail(email, sellerUid);
            String message = isSameAsSelf ? "이전과 동일한 이메일입니다." : "사용 가능한 이메일입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 사업자등록번호 중복 체크
    @PostMapping("/check-businessRegistrationNumber")
    public ResponseEntity<ApiResponse<String>> checkBusinessRegistrationNumber(@RequestBody Map<String, Object> body,
            Authentication authentication) {
        String businessRegistrationNumber = (String) body.get("businessRegistrationNumber");
        Long sellerUid = getUidFromRequest(body, "sellerUid");

        if (sellerUid == null && authentication != null) {
            String loginId = authentication.getName();
            sellerUid = sellerRepository.findBySellerId(loginId)
                                        .map(SellerEntity::getSellerUid)
                                        .orElse(null);
        }

        try {
            boolean isSameAsSelf = sellerService.checkBusinessRegistrationNumber(businessRegistrationNumber, sellerUid);
            String message = isSameAsSelf ? "이전과 동일한 사업자등록번호입니다." : "사용 가능한 사업자등록번호입니다.";
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 사용 중인")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 판매자 탈퇴(비활성화) PATCH 방식
    @PatchMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> sellerWithdraw(
            Authentication authentication,
            @RequestBody(required = false) Map<String, String> body) {
        String loginId = authentication.getName();
        String withdrawalReason = (body != null) ? body.getOrDefault("withdrawalReason", "") : "";
        sellerService.sellerWithdraw(loginId, withdrawalReason);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("회원 탈퇴(비활성화) 처리되었습니다."));
    }

    private Long getUidFromRequest(Map<String, Object> body, String key) {
        Object uidObj = body.get(key);
        if (uidObj == null) return null;
        if (uidObj instanceof Integer) {
            return ((Integer) uidObj).longValue();
        } else if (uidObj instanceof Long) {
            return (Long) uidObj;
        } else if (uidObj instanceof String) {
            try {
                return Long.parseLong((String) uidObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
