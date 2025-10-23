package it.back.buyer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO dto) {
        String jwt = buyerService.login(dto);
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
    //     String jwt = buyerService.login(dto);
    //     Map<String, String> responseBody = new HashMap<>();
    //     responseBody.put("message", "Login successful");
    //     responseBody.put("token", "bearer: " + jwt);
    //     return ResponseEntity.ok().body(responseBody);
    // }
    @GetMapping("/list")
    public ResponseEntity<List<BuyerDTO>> getAllBuyers() {
        return ResponseEntity.ok(buyerService.getAllBuyers());
    }

    // 회원 정보 부분 수정 (비밀번호, 닉네임, 전화번호, 주소, 상세주소, 생년월일, 성별)
    @PatchMapping("/{buyerUid}")
    public ResponseEntity<Void> updateBuyer(
            @PathVariable Long buyerUid,
            @RequestBody BuyerUpdateRequestDTO request,
            Authentication authentication) {
        try {
            buyerService.updateBuyer(buyerUid, request, authentication);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerBuyer(@RequestBody BuyerDTO buyerDto) {
        buyerService.registerBuyer(buyerDto);
        return ResponseEntity.ok("Buyer registered successfully");
    }

}
