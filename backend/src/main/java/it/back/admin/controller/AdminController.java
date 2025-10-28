package it.back.admin.controller;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import it.back.common.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import it.back.admin.dto.AdminResponseDTO;
import it.back.admin.entity.AdminEntity;
import it.back.admin.service.AdminService;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.dto.SellerDTO;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    // 로그인 한 admin 이 자신의 정보 불러오기 마이페이지 개인 정보 수정용
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdminResponseDTO>> getMyInfo(Authentication authentication) {
        String loginId = authentication.getName();
        System.out.println("[AdminController] /me loginId: " + loginId);
        AdminEntity admin = adminService.getAdminEntityById(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(new AdminResponseDTO(admin)));
    }

    // admin 전용 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequestDTO dto) {
        String jwt = adminService.login(dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("token", "Bearer: " + jwt);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseBody));
    }

    // admin 전용 buyer 리스트 불러오기 회원 보기 리스트 관리자 페이지 전용
    @GetMapping("/buyer/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<BuyerDTO>>> getAllBuyers(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String buyerEmail,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String withdrawalStatus) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(adminService.findAllBuyers(pageRequestDTO, buyerId, nickname, buyerEmail, phone, withdrawalStatus)));
    }

    // buyer 상세 보기 아마도 회원 리스트 표? 에서 링크 넣고 싶은데 넣고 이걸로 요청 보내서 상세 정보 보기
    @GetMapping("/buyer/{buyerUid}/detail")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> getBuyerDetail(@PathVariable Long buyerUid) {
        BuyerResponseDTO dto = buyerRepository.findById(buyerUid)
                .map(BuyerResponseDTO::new)
                .orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(dto));
    }

    // seller 리스트 보기 관리자 페이지 전용
    @GetMapping("/seller/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<SellerDTO>>> getAllSellers(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String sellerEmail,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String businessRegistrationNumber,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) String withdrawalStatus) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(adminService.findAllSellers(pageRequestDTO, sellerId, companyName, sellerEmail, phone, businessRegistrationNumber, isActive, isVerified, withdrawalStatus)));
    }

    // seller 상세 정보 보기
    @GetMapping("/seller/{sellerUid}/detail")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> getSellerDetail(@PathVariable Long sellerUid) {
        SellerResponseDTO dto = sellerRepository.findById(sellerUid)
                .map(SellerResponseDTO::new)
                .orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(dto));
    }

}
