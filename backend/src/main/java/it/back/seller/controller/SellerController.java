package it.back.seller.controller;

import it.back.common.dto.LoginRequestDTO;
import it.back.seller.dto.SellerDTO;
import it.back.seller.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

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
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String loginId, @RequestParam String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = sellerService.login(dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);
        return ResponseEntity.ok().body(responseBody);
    }
    // 또는
    // public ResponseEntity<?> login(@ModelAttribute LoginRequestDTO dto) { ... }
    */
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerDTO sellerDto) {
        sellerService.registerSeller(sellerDto);
        return ResponseEntity.ok("Seller registered successfully");
    }
}
