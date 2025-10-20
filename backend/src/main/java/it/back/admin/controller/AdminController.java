package it.back.admin.controller;

import it.back.admin.service.AdminService;
import it.back.common.dto.LoginRequestDTO;
import it.back.admin.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto) {
        String jwt = adminService.login(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return ResponseEntity.ok().headers(headers).body("Login successful");
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
