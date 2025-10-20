package it.back.admin.controller;

import it.back.admin.service.AdminService;
import it.back.common.dto.LoginRequestDTO;
import it.back.admin.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId, @RequestParam("password") String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        String jwt = adminService.login(dto);
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "Bearer " + jwt);
        
        return ResponseEntity.ok().body(responseBody);
    }


    @GetMapping("/admins")
    public ResponseEntity<List<UserSummaryDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.findAllAdmins());
    }


    @GetMapping("/buyers")
    public ResponseEntity<List<UserSummaryDTO>> getAllBuyers() {
        return ResponseEntity.ok(adminService.findAllBuyers());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<UserSummaryDTO>> getAllSellers() {
        return ResponseEntity.ok(adminService.findAllSellers());
    }
}
