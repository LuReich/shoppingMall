package it.back.sellerinquiry.service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.sellerinquiry.dto.AdminSellerInquiryAnswerRequestDTO;
import it.back.sellerinquiry.dto.SellerInquiryListResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryResponseDTO;
import it.back.sellerinquiry.entity.SellerInquiryEntity;
import it.back.sellerinquiry.repository.SellerInquiryRepository;
import it.back.sellerinquiry.specification.SellerInquirySpecification;
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
public class AdminSellerInquiryService {

    private final SellerInquiryRepository sellerInquiryRepository;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<SellerInquiryListResponseDTO> getSellerInquiryList(
            PageRequestDTO pageRequestDTO,
            SellerInquiryEntity.InquiryStatus inquiryStatus,
            SellerInquiryEntity.InquiryType inquiryType,
            String contentKeyword,
            String companyName,
            Long sellerUid) {

        Pageable pageable = pageRequestDTO.toPageable();

        Specification<SellerInquiryEntity> spec = Specification.allOf(
                SellerInquirySpecification.hasStatus(inquiryStatus),
                SellerInquirySpecification.hasInquiryType(inquiryType),
                SellerInquirySpecification.contentContains(contentKeyword),
                SellerInquirySpecification.companyNameContains(companyName),
                SellerInquirySpecification.hasSellerUid(sellerUid)
        );

        Page<SellerInquiryEntity> page = sellerInquiryRepository.findAll(spec, pageable);

        List<SellerInquiryListResponseDTO> dtoList = page.getContent().stream()
                .map(SellerInquiryListResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(page, dtoList);
    }

    @Transactional(readOnly = true)
    public SellerInquiryResponseDTO getSellerInquiry(Long inquiryId) {
        SellerInquiryEntity inquiry = sellerInquiryRepository.findByInquiryId(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));
        return SellerInquiryResponseDTO.fromEntity(inquiry);
    }

    public SellerInquiryResponseDTO answerSellerInquiry(Long inquiryId, String adminId, AdminSellerInquiryAnswerRequestDTO dto) {
        SellerInquiryEntity inquiry = sellerInquiryRepository.findByInquiryId(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (inquiry.getInquiryStatus() == SellerInquiryEntity.InquiryStatus.ANSWERED) {
            throw new IllegalArgumentException("이미 답변이 완료된 문의입니다.");
        }

        AdminEntity admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다."));

        inquiry.setAdmin(admin);
        inquiry.setAnswerContent(dto.getAnswerContent());
        inquiry.setInquiryStatus(SellerInquiryEntity.InquiryStatus.ANSWERED);
        inquiry.setAnswerAt(LocalDateTime.now());

        return SellerInquiryResponseDTO.fromEntity(sellerInquiryRepository.save(inquiry));
    }
}
