package it.back.buyer.controller;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.back.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;
    private final BuyerRepository buyerRepository;

    // 로그인한 buyer 가 자기 정보 보는 용도
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> getMyInfo(Authentication authentication) {
        String userId = authentication.getName();
        System.out.println("[BuyerController] /me userId: " + userId);
        BuyerEntity buyer = buyerRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 buyerId 없음: " + userId));
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
    // String jwt = buyerService.login(dto);
    // Map<String, String> responseBody = new HashMap<>();
    // responseBody.put("message", "Login successful");
    // responseBody.put("token", "bearer: " + jwt);
    // return ResponseEntity.ok().body(responseBody);
    // }

    // 그냥 있는 코드 권한 때문에 못씁니다.
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getAllBuyers() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(buyerService.getAllBuyers()));
    }

    // 회원 정보 부분 수정 (비밀번호, 닉네임, 전화번호, 주소, 상세주소, 생년월일, 성별)
    @PatchMapping("/{buyerUid}")
    public ResponseEntity<ApiResponse<String>> updateBuyer(
            @PathVariable Long buyerUid,
            @RequestBody BuyerUpdateRequestDTO request,
            Authentication authentication) {
        try {
            buyerService.updateBuyer(buyerUid, request, authentication);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Buyer updated successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.ok("Unauthorized"));
        }
    }

    // buyer 회원가입 용
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerBuyer(@RequestBody BuyerDTO buyerDto) {
        buyerService.registerBuyer(buyerDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Buyer registered successfully"));
    }

}
