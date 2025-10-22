package it.back.buyer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.buyer.dto.BuyerDTO;
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


    @PostMapping("/register")
    public ResponseEntity<String> registerBuyer(@RequestBody BuyerDTO buyerDto) {
        buyerService.registerBuyer(buyerDto);
        return ResponseEntity.ok("Buyer registered successfully");
    }

}
