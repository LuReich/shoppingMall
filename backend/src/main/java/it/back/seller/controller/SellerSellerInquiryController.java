package it.back.seller.controller;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryCreateRequestDTO;
import it.back.sellerinquiry.dto.SellerInquiryListResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryUpdateRequestDTO;
import it.back.sellerinquiry.service.SellerSellerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerSellerInquiryController {

    private final SellerSellerInquiryService sellerSellerInquiryService;

    @PostMapping("/create/sellerInquiry")
    public ResponseEntity<ApiResponse<SellerInquiryResponseDTO>> createSellerInquiry(
            Authentication authentication,
            @RequestPart("inquiryData") SellerInquiryCreateRequestDTO dto,
            @RequestPart(value = "addImages", required = false) List<MultipartFile> addImages) {

        SellerInquiryResponseDTO responseDTO = sellerSellerInquiryService.createSellerInquiry(
                authentication.getName(), dto, addImages);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/sellerInquiry/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<SellerInquiryListResponseDTO>>> getSellerInquiryList(
            Authentication authentication, @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageResponseDTO<SellerInquiryListResponseDTO> responseDTO = sellerSellerInquiryService.getSellerInquiryList(authentication.getName(), pageRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/sellerInquiry/{sellerInquiryId}")
    public ResponseEntity<ApiResponse<SellerInquiryResponseDTO>> getSellerInquiry(@PathVariable("sellerInquiryId") Long sellerInquiryId) {
        SellerInquiryResponseDTO responseDTO = sellerSellerInquiryService.getSellerInquiry(sellerInquiryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @PatchMapping("/update/sellerInquiry/{sellerInquiryId}")
    public ResponseEntity<ApiResponse<SellerInquiryResponseDTO>> updateSellerInquiry(
            @PathVariable("sellerInquiryId") Long sellerInquiryId,
            Authentication authentication,
            @RequestPart("inquiryData") SellerInquiryUpdateRequestDTO dto,
            @RequestPart(value = "addImages", required = false) List<MultipartFile> addImages) {
        SellerInquiryResponseDTO responseDTO = sellerSellerInquiryService.updateSellerInquiry(sellerInquiryId, authentication.getName(), dto, addImages);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @DeleteMapping("/delete/sellerInquiry/{sellerInquiryId}")
    public ResponseEntity<ApiResponse<String>> deleteSellerInquiry(
            @PathVariable("sellerInquiryId") Long sellerInquiryId,
            Authentication authentication) {
        sellerSellerInquiryService.deleteSellerInquiry(sellerInquiryId, authentication.getName());
        String message = sellerInquiryId + "번 문의를 삭제했습니다.";
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }
}
