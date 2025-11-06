package it.back.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.buyerinquiry.dto.AdminInquiryAnswerRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.service.AdminInquiryService;
import it.back.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/inquiry")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @PostMapping("/answer/{inquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> createAnswer(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestBody AdminInquiryAnswerRequestDTO answerData,
            Authentication authentication) {
        Long adminUid = getAdminUidFromAuth(authentication);
        BuyerInquiryResponseDTO inquiry = adminInquiryService.createAnswer(adminUid, inquiryId, answerData);
        return ResponseEntity.ok(ApiResponse.ok(inquiry));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BuyerInquiryListResponseDTO>>> getAllInquiries() {
        List<BuyerInquiryListResponseDTO> inquiries = adminInquiryService.getAllInquiries();
        return ResponseEntity.ok(ApiResponse.ok(inquiries));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> getInquiry(@PathVariable("inquiryId") Long inquiryId) {
        BuyerInquiryResponseDTO inquiry = adminInquiryService.getInquiryByIdForAdmin(inquiryId);
        return ResponseEntity.ok(ApiResponse.ok(inquiry));
    }

    private Long getAdminUidFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object uidObj = details.get("uid");
        if (uidObj instanceof Integer i) {
            return i.longValue();
        } else if (uidObj instanceof Long l) {
            return l;
        } else {
            throw new IllegalStateException("uid 타입이 올바르지 않습니다.");
        }
    }
}
