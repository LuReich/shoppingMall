package it.back.buyer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.back.admin.dto.UserSummaryDTO;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerUpdateRequest;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {


    private final BuyerService buyerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId, @RequestParam("password") String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = buyerService.login(dto);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);

        return ResponseEntity.ok().body(responseBody);
    }

    // 회원 정보 부분 수정 (비밀번호, 닉네임, 전화번호, 주소, 상세주소, 생년월일, 성별)

    @PatchMapping("/{buyerUid}")
    public ResponseEntity<Void> updateBuyer(
            @PathVariable Long buyerUid,
            @RequestBody BuyerUpdateRequest request,
            Authentication authentication) {
        // principal이 null이면 401 반환
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (!(principal instanceof UserSummaryDTO user)) {
            return ResponseEntity.status(401).build();
        }
        String loginId = user.getLoginId();
        String role = user.getRole();
        buyerService.updateBuyer(buyerUid, request, loginId, role);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerBuyer(@RequestBody BuyerDTO buyerDto) {
        buyerService.registerBuyer(buyerDto);
        return ResponseEntity.ok("Buyer registered successfully");
    }

}
