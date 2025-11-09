package it.back.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.buyerinquiry.dto.AdminBuyerInquiryAnswerRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import it.back.buyerinquiry.service.AdminBuyerInquiryService;
import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminBuyerInquiryController {

    private final AdminBuyerInquiryService adminBuyerInquiryService;

    @GetMapping("/buyerInquiry/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<BuyerInquiryListResponseDTO>>> getBuyerInquiryList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @RequestParam(value = "inquiryStatus", required = false) BuyerInquiryEntity.InquiryStatus inquiryStatus,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "buyerUid", required = false) Long buyerUid) {
        PageResponseDTO<BuyerInquiryListResponseDTO> responseDTO = adminBuyerInquiryService.getBuyerInquiryList(
                pageRequestDTO, inquiryStatus, contentKeyword, nickname, buyerUid);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/buyerInquiry/{buyerInquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> getBuyerInquiry(@PathVariable("buyerInquiryId") Long buyerInquiryId) {
        BuyerInquiryResponseDTO responseDTO = adminBuyerInquiryService.getBuyerInquiry(buyerInquiryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @PatchMapping("/answer/buyerInquiry/{buyerInquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> answerBuyerInquiry(@PathVariable("buyerInquiryId") Long buyerInquiryId,
            Authentication authentication,
            @RequestBody AdminBuyerInquiryAnswerRequestDTO dto) {
        BuyerInquiryResponseDTO responseDTO = adminBuyerInquiryService.answerBuyerInquiry(buyerInquiryId, authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }
}
