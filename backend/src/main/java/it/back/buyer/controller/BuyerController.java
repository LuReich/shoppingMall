package it.back.buyer.controller;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.service.BuyerService;
import it.back.common.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
public class BuyerController {

    @GetMapping("/list")
    public ResponseEntity<java.util.List<BuyerDTO>> getAllBuyers() {
        java.util.List<BuyerDTO> dtos = buyerService.getAllBuyers().stream()
            .map(buyer -> {
                BuyerDTO dto = new BuyerDTO();
                dto.setBuyerId(buyer.getBuyerId());
                dto.setNickname(buyer.getNickname());
                dto.setPassword(null); // 비밀번호는 반환하지 않음
                if (buyer.getBuyerDetail() != null) {
                    dto.setPhoneNumber(buyer.getBuyerDetail().getPhoneNumber());
                    dto.setAddress(buyer.getBuyerDetail().getAddress());
                    dto.setAddressDetail(buyer.getBuyerDetail().getAddressDetail());
                }
                return dto;
            })
            .toList();
        return ResponseEntity.ok(dtos);
    }

    private final BuyerService buyerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId, @RequestParam("password") String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = buyerService.login(dto);
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "Bearer " + jwt);
        
        return ResponseEntity.ok().body(responseBody);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerBuyer(@RequestBody BuyerDTO buyerDto) {
        buyerService.registerBuyer(buyerDto);
        return ResponseEntity.ok("Buyer registered successfully");
    }
}
