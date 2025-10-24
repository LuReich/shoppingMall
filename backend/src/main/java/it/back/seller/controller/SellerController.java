package it.back.seller.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.dto.LoginRequestDTO;
import it.back.seller.dto.SellerDTO;
import it.back.seller.dto.SellerPublicDTO;
import it.back.seller.service.SellerService;
import it.back.seller.repository.SellerRepository;
import it.back.seller.entity.SellerEntity;
import it.back.seller.dto.SellerResponseDTO;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    

    private final SellerService sellerService;
    private final SellerRepository sellerRepository;

    // 로그인한 seller 가 자기 정보 불러오기
    @GetMapping("/me")
    public ResponseEntity<SellerResponseDTO> getMyInfo(Authentication authentication) {
        String userId = authentication.getName();
        System.out.println("[SellerController] /me userId: " + userId);
        SellerEntity seller = sellerRepository.findBySellerId(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 sellerId 없음: " + userId));
        return ResponseEntity.ok(new SellerResponseDTO(seller));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO dto) {
        String jwt = sellerService.login(dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);
        return ResponseEntity.ok().body(responseBody);
    }

    /*
    // [form-data 방식으로 바꾸고 싶을 때 참고]
    
    // 또는
    // public ResponseEntity<?> login(@ModelAttribute LoginRequestDTO dto) { ... }
     */
    // @PostMapping("/login")
    // public ResponseEntity<Map<String, String>> login(@RequestParam String loginId, @RequestParam String password) {
    //     LoginRequestDTO dto = new LoginRequestDTO();
    //     dto.setLoginId(loginId);
    //     dto.setPassword(password);
    //     String jwt = sellerService.login(dto);
    //     Map<String, String> responseBody = new HashMap<>();
    //     responseBody.put("message", "Login successful");
    //     responseBody.put("token", "bearer: " + jwt);
    //     return ResponseEntity.ok().body(responseBody);
    // }

    // 공개용 판매자 정보 조회 (비로그인/로그인 모두 접근 가능)
    @GetMapping("/public/{sellerUid}")
    public ResponseEntity<SellerPublicDTO> getSellerPublicInfo(@PathVariable Long sellerUid) {
        return ResponseEntity.ok(sellerService.getSellerPublicInfo(sellerUid));
    }


    // 테스트용 미사용 권장
    @GetMapping("/list")
    public ResponseEntity<List<SellerDTO>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    // seller 등록용
    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerDTO sellerDTO) {
        sellerService.registerSeller(sellerDTO);
        return ResponseEntity.ok("Seller registered successfully");
    }
}
