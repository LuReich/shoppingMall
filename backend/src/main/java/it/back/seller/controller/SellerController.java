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
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    @GetMapping("/list")
    public ResponseEntity<java.util.List<SellerDTO>> getAllSellers() {
        java.util.List<SellerDTO> dtos = sellerService.getAllSellers().stream()
            .map(seller -> {
                SellerDTO dto = new SellerDTO();
                dto.setSellerId(seller.getSellerId());
                dto.setCompanyName(seller.getCompanyName());
                dto.setIsVerified(seller.isVerified());
                dto.setIsActive(seller.isActive());
                dto.setPassword(null); // 비밀번호는 반환하지 않음
                if (seller.getSellerDetail() != null) {
                    dto.setBusinessRegistrationNumber(seller.getSellerDetail().getBusinessRegistrationNumber());
                    dto.setPhone(seller.getSellerDetail().getPhone());
                    dto.setAddress(seller.getSellerDetail().getAddress());
                    dto.setAddressDetail(seller.getSellerDetail().getAddressDetail());
                }
                return dto;
            })
            .toList();
        return ResponseEntity.ok(dtos);
    }

    private final SellerService sellerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId, @RequestParam("password") String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = sellerService.login(dto);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "bearer: " + jwt);

        return ResponseEntity.ok().body(responseBody);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerDTO sellerDto) {
        sellerService.registerSeller(sellerDto);
        return ResponseEntity.ok("Seller registered successfully");
    }
}
