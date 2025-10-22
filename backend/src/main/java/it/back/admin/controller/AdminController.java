

package it.back.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import it.back.admin.dto.UserSummaryDTO;
import it.back.admin.service.AdminService;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.seller.service.SellerService;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.buyer.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {
    private final AdminService adminService;
    private final SellerService sellerService;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    

    

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {
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

    @GetMapping("/buyer/list")
    public ResponseEntity<PageResponseDTO<BuyerResponseDTO>> getAllBuyers(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<BuyerEntity> page = adminService.findAllBuyers(pageable);
        List<BuyerResponseDTO> buyerList = page.getContent().stream()
                .map(BuyerResponseDTO::new)
                .collect(Collectors.toList());

        PageResponseDTO<BuyerResponseDTO> response = new PageResponseDTO<>(
                buyerList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buyer/{buyerUid}/detail")
    public ResponseEntity<BuyerResponseDTO> getBuyerDetail(@PathVariable Long buyerUid) {
        return buyerRepository.findById(buyerUid)
                .map(BuyerResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/seller/list")
    public ResponseEntity<PageResponseDTO<SellerResponseDTO>> getAllSellersPage(@PageableDefault(size = 10) Pageable pageable) {
    Page<SellerEntity> page = sellerRepository.findAll(pageable);
    List<SellerResponseDTO> sellerList = page.getContent().stream()
        .map(SellerResponseDTO::new)
        .collect(Collectors.toList());
    PageResponseDTO<SellerResponseDTO> response = new PageResponseDTO<>(
        sellerList,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast());
    return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/{sellerUid}/detail")
    public ResponseEntity<SellerResponseDTO> getSellerDetail(@PathVariable Long sellerUid) {
    return sellerRepository.findById(sellerUid)
        .map(SellerResponseDTO::new)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

}
