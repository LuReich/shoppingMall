package it.back.buyerinquiry.service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyerinquiry.dto.AdminInquiryAnswerRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.entity.BuyerInquiry;
import it.back.buyerinquiry.entity.InquiryAnswer;
import it.back.buyerinquiry.entity.InquiryStatus;
import it.back.buyerinquiry.repository.BuyerInquiryRepository;
import it.back.buyerinquiry.repository.InquiryAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {

    private final BuyerInquiryRepository buyerInquiryRepository;
    private final AdminRepository adminRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;

    @Transactional
    public BuyerInquiryResponseDTO createAnswer(Long adminUid, Long inquiryId, AdminInquiryAnswerRequestDTO dto) {
        AdminEntity admin = adminRepository.findById(adminUid.intValue())
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        BuyerInquiry inquiry = buyerInquiryRepository.findByIdWithDetails(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
            throw new IllegalStateException("이미 답변이 완료된 문의입니다.");
        }

        InquiryAnswer answer = InquiryAnswer.builder()
                .buyerInquiry(inquiry)
                .admin(admin)
                .content(dto.getContent())
                .build();
        
        inquiryAnswerRepository.save(answer);

        inquiry.setStatus(InquiryStatus.ANSWERED);
        inquiry.setAnswer(answer);
        
        buyerInquiryRepository.save(inquiry);

        return new BuyerInquiryResponseDTO(inquiry);
    }

    @Transactional(readOnly = true)
    public List<BuyerInquiryListResponseDTO> getAllInquiries() {
        return buyerInquiryRepository.findAll().stream()
                .map(BuyerInquiryListResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuyerInquiryResponseDTO getInquiryByIdForAdmin(Long inquiryId) {
        BuyerInquiry inquiry = buyerInquiryRepository.findByIdWithDetails(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        return new BuyerInquiryResponseDTO(inquiry);
    }
}
