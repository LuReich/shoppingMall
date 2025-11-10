package it.back.faq.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.faq.dto.FaqCreateRequestDTO;
import it.back.faq.dto.FaqDetailResponseDTO;
import it.back.faq.dto.FaqListResponseDTO;
import it.back.faq.dto.FaqUpdateRequestDTO;
import it.back.faq.entity.FaqEntity;
import it.back.faq.service.FaqService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<FaqListResponseDTO>>> getFaqs(
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "faqTarget", required = false) FaqEntity.FaqTarget faqTarget,
            @RequestParam(name = "faqCategory", required = false) FaqEntity.FaqCategory faqCategory,
            @RequestParam(name = "keyword", required = false) String keyword) {

        PageResponseDTO<FaqListResponseDTO> faqs = faqService.getFaqs(pageRequestDTO, faqTarget, faqCategory, keyword);
        return ResponseEntity.ok(ApiResponse.ok(faqs));
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<ApiResponse<FaqDetailResponseDTO>> getFaqById(@PathVariable("faqId") Integer faqId) {
        FaqDetailResponseDTO faq = faqService.getFaqById(faqId);
        return ResponseEntity.ok(ApiResponse.ok(faq));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FaqDetailResponseDTO>> createFaq(
            @RequestBody FaqCreateRequestDTO requestDto,
            Authentication authentication) {

        Integer adminUid = getAdminUidFromAuthWithRoleCheck(authentication);
        FaqDetailResponseDTO createdFaq = faqService.createFaq(requestDto, adminUid);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(createdFaq));
    }

    @PatchMapping("/update/{faqId}")
    public ResponseEntity<ApiResponse<FaqDetailResponseDTO>> updateFaq(
            @PathVariable("faqId") Integer faqId,
            @RequestBody FaqUpdateRequestDTO requestDto,
            Authentication authentication) {

        Integer adminUid = getAdminUidFromAuthWithRoleCheck(authentication);
        FaqDetailResponseDTO updatedFaq = faqService.updateFaq(faqId, requestDto, adminUid);
        return ResponseEntity.ok(ApiResponse.ok(updatedFaq));
    }

    @DeleteMapping("/delete/{faqId}")
    public ResponseEntity<ApiResponse<String>> deleteFaq(
            @PathVariable("faqId") Integer faqId,
            Authentication authentication) {

        getAdminUidFromAuthWithRoleCheck(authentication); // Check for admin role
        faqService.deleteFaq(faqId);
        String message = String.format("%d번 FAQ를 삭제했습니다.", faqId);
        return ResponseEntity.ok(ApiResponse.ok(message));
    }

    private Integer getAdminUidFromAuthWithRoleCheck(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> details)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }
        Object roleObj = details.get("role");
        // For now, any "ADMIN" role is sufficient.
        if (roleObj == null || !roleObj.toString().contains("ADMIN")) {
            throw new IllegalStateException("관리자(ADMIN) 권한이 필요합니다.");
        }
        Object uidObj = details.get("uid");
        if (uidObj instanceof Integer i) {
            return i;
        } else if (uidObj instanceof Long l) {
            return l.intValue();
        } else {
            throw new IllegalStateException("uid 타입이 올바르지 않습니다. Integer 또는 Long 타입이 필요합니다.");
        }
    }
}
