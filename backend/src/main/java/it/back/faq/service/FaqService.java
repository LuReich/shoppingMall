package it.back.faq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.faq.dto.FaqCreateRequestDTO;
import it.back.faq.dto.FaqDetailResponseDTO;
import it.back.faq.dto.FaqListResponseDTO;
import it.back.faq.dto.FaqUpdateRequestDTO;
import it.back.faq.entity.FaqEntity;
import it.back.faq.repository.FaqRepository;
import it.back.faq.specification.FaqSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<FaqListResponseDTO> getFaqs(
            PageRequestDTO pageRequestDTO,
            FaqEntity.FaqTarget faqTarget,
            FaqEntity.FaqCategory faqCategory,
            String keyword) {

        
        Pageable pageable = pageRequestDTO.toPageable();
        Specification<FaqEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (faqTarget != null) {
            spec = spec.and(FaqSpecification.equalFaqTarget(faqTarget));
        }
        if (faqCategory != null) {
            spec = spec.and(FaqSpecification.equalFaqCategory(faqCategory));
        }
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(FaqSpecification.searchByKeyword(keyword));
        }

        Page<FaqEntity> page = faqRepository.findAll(spec, pageable);

        List<FaqListResponseDTO> dtos = page.getContent().stream()
                .map(this::convertToFaqListDto)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(page, dtos);
    }

    @Transactional(readOnly = true)
    public FaqDetailResponseDTO getFaqById(Integer faqId) {
        FaqEntity faqEntity = faqRepository.findById(faqId)
                .orElseThrow(() -> new IllegalArgumentException("FAQ not found with id: " + faqId));
        return convertToDetailDto(faqEntity);
    }

    @Transactional
    public FaqDetailResponseDTO createFaq(FaqCreateRequestDTO requestDto, Integer adminUid) {
        AdminEntity admin = adminRepository.findById(adminUid)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with id: " + adminUid));

        FaqEntity faqEntity = new FaqEntity();
        faqEntity.setAdmin(admin);
        faqEntity.setFaqTarget(requestDto.getFaqTarget());
        faqEntity.setFaqCategory(requestDto.getFaqCategory());
        faqEntity.setFaqQuestion(requestDto.getFaqQuestion());
        faqEntity.setFaqAnswer(requestDto.getFaqAnswer());
        if (requestDto.getSortOrder() != null) {
            faqEntity.setSortOrder(requestDto.getSortOrder());
        }

        FaqEntity savedFaq = faqRepository.save(faqEntity);

        return convertToDetailDto(savedFaq);
    }

    @Transactional
    public FaqDetailResponseDTO updateFaq(Integer faqId, FaqUpdateRequestDTO requestDto, Integer adminUid) {
        FaqEntity faqEntity = faqRepository.findById(faqId)
                .orElseThrow(() -> new IllegalArgumentException("FAQ not found with id: " + faqId));

        AdminEntity admin = adminRepository.findById(adminUid)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with id: " + adminUid));

        faqEntity.setAdmin(admin);

        if (requestDto.getFaqTarget() != null) {
            faqEntity.setFaqTarget(requestDto.getFaqTarget());
        }
        if (requestDto.getFaqCategory() != null) {
            faqEntity.setFaqCategory(requestDto.getFaqCategory());
        }
        if (requestDto.getFaqQuestion() != null) {
            faqEntity.setFaqQuestion(requestDto.getFaqQuestion());
        }
        if (requestDto.getFaqAnswer() != null) {
            faqEntity.setFaqAnswer(requestDto.getFaqAnswer());
        }
        if (requestDto.getSortOrder() != null) {
            faqEntity.setSortOrder(requestDto.getSortOrder());
        }

        FaqEntity updatedFaq = faqRepository.save(faqEntity);

        return convertToDetailDto(updatedFaq);
    }

    @Transactional
    public void deleteFaq(Integer faqId) {
        if (!faqRepository.existsById(faqId)) {
            throw new IllegalArgumentException("FAQ not found with id: " + faqId);
        }
        faqRepository.deleteById(faqId);
    }

    private FaqDetailResponseDTO convertToDetailDto(FaqEntity faqEntity) {
        FaqDetailResponseDTO responseDto = new FaqDetailResponseDTO();
        responseDto.setFaqId(faqEntity.getFaqId());
        responseDto.setFaqTarget(faqEntity.getFaqTarget().name());
        responseDto.setFaqCategory(faqEntity.getFaqCategory().name());
        responseDto.setFaqQuestion(faqEntity.getFaqQuestion());
        responseDto.setFaqAnswer(faqEntity.getFaqAnswer());
        responseDto.setCreateAt(faqEntity.getCreateAt());
        responseDto.setUpdateAt(faqEntity.getUpdateAt());
        return responseDto;
    }

    private FaqListResponseDTO convertToFaqListDto(FaqEntity faqEntity) {
        FaqListResponseDTO listDto = new FaqListResponseDTO();
        listDto.setFaqId(faqEntity.getFaqId());
        listDto.setFaqTarget(faqEntity.getFaqTarget().name());
        listDto.setFaqCategory(faqEntity.getFaqCategory().name());
        listDto.setFaqQuestion(faqEntity.getFaqQuestion());
        listDto.setFaqAnswer(faqEntity.getFaqAnswer());
        listDto.setSortOrder(faqEntity.getSortOrder());
        listDto.setCreateAt(faqEntity.getCreateAt());
        listDto.setUpdateAt(faqEntity.getUpdateAt());
        return listDto;
    }
}
