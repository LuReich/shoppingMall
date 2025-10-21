package it.back.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.admin.dto.UserSummaryDTO;
import it.back.admin.service.AdminService;
import it.back.common.dto.LoginRequestDTO;
import it.back.seller.service.SellerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final SellerService sellerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId, @RequestParam("password") String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = adminService.login(dto);
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "Bearer: " + jwt);
        
        return ResponseEntity.ok().body(responseBody);
    }


    @GetMapping("/admin")
    public ResponseEntity<List<UserSummaryDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.findAllAdmins());
    }


    @GetMapping("/buyer")
    public ResponseEntity<List<UserSummaryDTO>> getAllBuyers() {
        return ResponseEntity.ok(adminService.findAllBuyers());
    }

    @GetMapping("/seller")
    public ResponseEntity<List<UserSummaryDTO>> getAllSellers() {
        return ResponseEntity.ok(adminService.findAllSellers());
    }

    
}
