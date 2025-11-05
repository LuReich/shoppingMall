package it.back.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping; // New import
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.admin.dto.AdminResponseDTO;
import it.back.admin.dto.AdminUpdateBuyerRequestDTO; // New import
import it.back.admin.dto.AdminUpdateSellerRequestDTO;
import it.back.admin.entity.AdminEntity;
import it.back.admin.service.AdminService;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyer.service.BuyerService; // New import
import it.back.common.dto.ApiResponse;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDeletedByAdminRequestDTO;
import it.back.product.service.ProductService;
import it.back.seller.dto.SellerDTO;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.repository.SellerRepository;
import it.back.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final BuyerService buyerService; // Inject BuyerService
    private final SellerService sellerService; // Inject SellerService
    private final ProductService productService;

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
            @RequestParam(name = "buyerUid", required = false) Long buyerUid,
            @RequestParam(name = "buyerId", required = false) String buyerId,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "buyerEmail", required = false) String buyerEmail,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "withdrawalStatus", required = false) String withdrawalStatus) {
        PageResponseDTO<BuyerDTO> buyerPageResponse = adminService.findAllBuyers(pageRequestDTO, buyerUid, buyerId, nickname, buyerEmail, phone, isActive, withdrawalStatus);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(buyerPageResponse));
    }

    // buyer 상세 보기 아마도 회원 리스트 표? 에서 링크 넣고 싶은데 넣고 이걸로 요청 보내서 상세 정보 보기
    @GetMapping("/buyer/{buyerUid}/detail")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> getBuyerDetail(@PathVariable("buyerUid") Long buyerUid) {
        BuyerResponseDTO dto = buyerRepository.findById(buyerUid)
                .map(BuyerResponseDTO::new)
                .orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(dto));
    }

    // admin 전용 buyer 정보 수정 (UID 제외) 및 추방 조치
    @PatchMapping("/update/buyer/{buyerUid}")
    public ResponseEntity<ApiResponse<BuyerResponseDTO>> adminUpdateBuyer(
            @PathVariable("buyerUid") Long buyerUid,
            @Valid @RequestBody AdminUpdateBuyerRequestDTO request) {
        BuyerResponseDTO updatedBuyer = buyerService.adminUpdateBuyer(buyerUid, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedBuyer));
    }

    // admin 전용 seller 정보 수정 (UID 제외)
    @PatchMapping("/update/seller/{sellerUid}")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> adminUpdateSeller(
            @PathVariable("sellerUid") Long sellerUid,
            @Valid @RequestBody AdminUpdateSellerRequestDTO request) {
        SellerResponseDTO updatedSeller = sellerService.adminUpdateSeller(sellerUid, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedSeller));
    }

    // seller 리스트 보기 관리자 페이지 전용
    @GetMapping("/seller/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<SellerDTO>>> getAllSellers(
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "sellerUid", required = false) Long sellerUid,
            @RequestParam(name = "sellerId", required = false) String sellerId,
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(name = "sellerEmail", required = false) String sellerEmail,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "businessRegistrationNumber", required = false) String businessRegistrationNumber,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "isVerified", required = false) Boolean isVerified,
            @RequestParam(name = "withdrawalStatus", required = false) String withdrawalStatus) {
        PageResponseDTO<SellerDTO> sellerPageResponse = adminService.findAllSellers(pageRequestDTO, sellerUid, sellerId, companyName, sellerEmail, phone, businessRegistrationNumber, isActive, isVerified, withdrawalStatus);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(sellerPageResponse));
    }

    // seller 상세 정보 보기
    @GetMapping("/seller/{sellerUid}/detail")
    public ResponseEntity<ApiResponse<SellerResponseDTO>> getSellerDetail(@PathVariable("sellerUid") Long sellerUid) {
        SellerResponseDTO dto = sellerRepository.findById(sellerUid)
                .map(SellerResponseDTO::new)
                .orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(dto));
    }

    // admin 전용 상품 삭제 상태 변경 (soft delete, restore)
    @PatchMapping("/product/change-deletion-status/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductDeletionStatus(
            @PathVariable("productId") Long productId,
            @RequestBody ProductDeletedByAdminRequestDTO requestDTO) {
        ProductDTO updatedProduct = productService.updateProductDeletionStatusByAdmin(productId, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedProduct));
    }

}
