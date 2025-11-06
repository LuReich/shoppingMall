package it.back.buyer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.back.buyerinquiry.dto.BuyerInquiryCreateRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.service.BuyerInquiryService;
import it.back.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerInquiryController {

    private final BuyerInquiryService buyerInquiryService;

    @PostMapping("/create/inquiry")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> createInquiry(
            Authentication authentication,
            @RequestPart("inquiryData") BuyerInquiryCreateRequestDTO inquiryData,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) {

        Long buyerUid = getBuyerUidFromAuth(authentication);
        BuyerInquiryResponseDTO createdInquiry = buyerInquiryService.createInquiry(buyerUid, inquiryData, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(createdInquiry));
    }

    @GetMapping("/inquiry/list")
    public ResponseEntity<ApiResponse<List<BuyerInquiryListResponseDTO>>> getMyInquiries(Authentication authentication) {
        Long buyerUid = getBuyerUidFromAuth(authentication);
        List<BuyerInquiryListResponseDTO> inquiries = buyerInquiryService.getInquiriesByBuyer(buyerUid);
        return ResponseEntity.ok(ApiResponse.ok(inquiries));
    }

    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> getInquiry(
            @PathVariable("inquiryId") Long inquiryId,
            Authentication authentication) {
        Long buyerUid = getBuyerUidFromAuth(authentication);
        BuyerInquiryResponseDTO inquiry = buyerInquiryService.getInquiryById(inquiryId, buyerUid);
        return ResponseEntity.ok(ApiResponse.ok(inquiry));
    }

    private Long getBuyerUidFromAuth(Authentication authentication) {
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
