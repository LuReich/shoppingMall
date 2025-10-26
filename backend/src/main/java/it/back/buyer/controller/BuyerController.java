package it.back.buyer.controller;

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

import it.back.buyer.dto.BuyerRegisterDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.ApiResponse;
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
    @PatchMapping("/{buyerUid}")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> updateBuyer(
            @PathVariable Long buyerUid,
            @RequestBody BuyerUpdateRequestDTO request,
            Authentication authentication) {

        BuyerEntity updatedEntity = buyerService.updateBuyer(buyerUid, request, authentication);
        BuyerResponseDTO updatedBuyer = new BuyerResponseDTO(updatedEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedBuyer));
    }

    // 이메일 중복 및 형식 체크
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestBody Map<String, String> body,
            Authentication authentication) {

        String email = body.get("email");
        String loginId = authentication != null ? authentication.getName() : null;
        String result = buyerService.checkEmail(email, loginId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 전화번호 중복 및 형식 체크
    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse<String>> checkPhone(@RequestBody Map<String, String> body, Authentication authentication) {
        String phone = body.get("phone");
        String loginId = authentication != null ? authentication.getName() : null;
        String result = buyerService.checkPhone(phone, loginId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // buyer 회원가입 용
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> registerBuyer(@RequestBody BuyerRegisterDTO buyerRegisterDto) {
        BuyerResponseDTO result = buyerService.registerBuyer(buyerRegisterDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

}
