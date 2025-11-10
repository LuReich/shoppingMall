package it.back.admin.controller;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.sellerinquiry.dto.AdminSellerInquiryAnswerRequestDTO;
import it.back.sellerinquiry.dto.SellerInquiryListResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryResponseDTO;
import it.back.sellerinquiry.entity.SellerInquiryEntity;
import it.back.sellerinquiry.service.AdminSellerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminSellerInquiryController {

    private final AdminSellerInquiryService adminSellerInquiryService;

    @GetMapping("/sellerInquiry/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<SellerInquiryListResponseDTO>>> getSellerInquiryList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @RequestParam(value = "inquiryStatus", required = false) SellerInquiryEntity.InquiryStatus inquiryStatus,
            @RequestParam(value = "inquiryType", required = false) SellerInquiryEntity.InquiryType inquiryType,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "sellerUid", required = false) Long sellerUid) {
        PageResponseDTO<SellerInquiryListResponseDTO> responseDTO = adminSellerInquiryService.getSellerInquiryList(
                pageRequestDTO, inquiryStatus, inquiryType, contentKeyword, companyName, sellerUid);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/sellerInquiry/{sellerInquiryId}")
    public ResponseEntity<ApiResponse<SellerInquiryResponseDTO>> getSellerInquiry(@PathVariable("sellerInquiryId") Long sellerInquiryId) {
        SellerInquiryResponseDTO responseDTO = adminSellerInquiryService.getSellerInquiry(sellerInquiryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @PatchMapping("/answer/sellerInquiry/{sellerInquiryId}")
    public ResponseEntity<ApiResponse<SellerInquiryResponseDTO>> answerSellerInquiry(
            @PathVariable("sellerInquiryId") Long sellerInquiryId,
            Authentication authentication,
            @RequestBody AdminSellerInquiryAnswerRequestDTO dto) {
        SellerInquiryResponseDTO responseDTO = adminSellerInquiryService.answerSellerInquiry(sellerInquiryId, authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }
}
