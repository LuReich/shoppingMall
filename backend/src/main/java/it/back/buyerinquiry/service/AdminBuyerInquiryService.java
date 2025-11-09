package it.back.buyerinquiry.service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyerinquiry.dto.AdminBuyerInquiryAnswerRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import it.back.buyerinquiry.repository.BuyerInquiryRepository;
import it.back.buyerinquiry.specification.BuyerInquirySpecification;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBuyerInquiryService {

    private final BuyerInquiryRepository buyerInquiryRepository;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<BuyerInquiryListResponseDTO> getBuyerInquiryList(
            PageRequestDTO pageRequestDTO,
            BuyerInquiryEntity.InquiryStatus inquiryStatus,
            String contentKeyword,
            String nickname,
            Long buyerUid) {

        Pageable pageable = pageRequestDTO.toPageable();

        Specification<BuyerInquiryEntity> spec = Specification.where(BuyerInquirySpecification.hasStatus(inquiryStatus))
                .and(BuyerInquirySpecification.contentContains(contentKeyword))
                .and(BuyerInquirySpecification.nicknameContains(nickname))
                .and(BuyerInquirySpecification.hasBuyerUid(buyerUid));

        Page<BuyerInquiryEntity> page = buyerInquiryRepository.findAll(spec, pageable);

        List<BuyerInquiryListResponseDTO> dtoList = page.getContent().stream()
                .map(BuyerInquiryListResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(page, dtoList);
    }

    @Transactional(readOnly = true)
    public BuyerInquiryResponseDTO getBuyerInquiry(Long inquiryId) {
        BuyerInquiryEntity inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));
        return BuyerInquiryResponseDTO.fromEntity(inquiry);
    }

    public BuyerInquiryResponseDTO answerBuyerInquiry(Long inquiryId, String adminId, AdminBuyerInquiryAnswerRequestDTO dto) {
        BuyerInquiryEntity inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (inquiry.getInquiryStatus() == BuyerInquiryEntity.InquiryStatus.ANSWERED) {
            throw new IllegalArgumentException("이미 답변이 완료된 문의입니다.");
        }

        AdminEntity admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다."));

        inquiry.setAdmin(admin);
        inquiry.setAnswerContent(dto.getAnswerContent());
        inquiry.setInquiryStatus(BuyerInquiryEntity.InquiryStatus.ANSWERED);
        inquiry.setAnswerAt(LocalDateTime.now());

        return BuyerInquiryResponseDTO.fromEntity(buyerInquiryRepository.save(inquiry));
    }
}
