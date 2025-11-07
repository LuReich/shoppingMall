package it.back.buyer.controller;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryCreateRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryUpdateRequestDTO;
import it.back.buyerinquiry.service.BuyerBuyerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerBuyerInquiryController {

    private final BuyerBuyerInquiryService buyerBuyerInquiryService;

    @PostMapping("/create/buyerInquiry")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> createBuyerInquiry(Authentication authentication, @ModelAttribute BuyerInquiryCreateRequestDTO dto, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        BuyerInquiryResponseDTO responseDTO = buyerBuyerInquiryService.createBuyerInquiry(authentication.getName(), dto, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/buyerInquiry/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<BuyerInquiryListResponseDTO>>> getBuyerInquiryList(Authentication authentication, @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BuyerInquiryListResponseDTO> responseDTO = buyerBuyerInquiryService.getBuyerInquiryList(authentication.getName(), pageRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @GetMapping("/buyerInquiry/{buyerInquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> getBuyerInquiry(@PathVariable("buyerInquiryId") Long buyerInquiryId) {
        BuyerInquiryResponseDTO responseDTO = buyerBuyerInquiryService.getBuyerInquiry(buyerInquiryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @PatchMapping("/update/buyerInquiry/{buyerInquiryId}")
    public ResponseEntity<ApiResponse<BuyerInquiryResponseDTO>> updateBuyerInquiry(@PathVariable("buyerInquiryId") Long buyerInquiryId,
            Authentication authentication,
            @ModelAttribute BuyerInquiryUpdateRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        BuyerInquiryResponseDTO responseDTO = buyerBuyerInquiryService.updateBuyerInquiry(buyerInquiryId, authentication.getName(), dto, files);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseDTO));
    }

    @DeleteMapping("/delete/buyerInquiry/{buyerInquiryId}")
    public ResponseEntity<ApiResponse<Void>> deleteBuyerInquiry(@PathVariable("buyerInquiryId") Long buyerInquiryId,
            Authentication authentication) {
        buyerBuyerInquiryService.deleteBuyerInquiry(buyerInquiryId, authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(null));
    }
}
