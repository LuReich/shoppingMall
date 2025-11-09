package it.back.buyerinquiry.service;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyerinquiry.dto.BuyerInquiryCreateRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryUpdateRequestDTO;
import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import it.back.buyerinquiry.entity.BuyerInquiryImageEntity;
import it.back.buyerinquiry.repository.BuyerInquiryRepository;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerBuyerInquiryService {

    private final BuyerInquiryRepository buyerInquiryRepository;
    private final BuyerRepository buyerRepository;
    private final FileUtils fileUtils;

    private final String uploadPath = "C:/ourshop/buyerinquiry/";

    public BuyerInquiryResponseDTO createBuyerInquiry(String userId, BuyerInquiryCreateRequestDTO dto, List<MultipartFile> images) {
        BuyerEntity buyer = buyerRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        BuyerInquiryEntity inquiry = new BuyerInquiryEntity();
        inquiry.setBuyer(buyer);
        inquiry.setInquiryType(dto.getInquiryType());
        inquiry.setTitle(dto.getTitle());
        inquiry.setQuestionContent(dto.getQuestionContent());
        inquiry.setInquiryStatus(BuyerInquiryEntity.InquiryStatus.PENDING);

        BuyerInquiryEntity savedInquiry = buyerInquiryRepository.save(inquiry);

        if (images != null && !images.isEmpty()) {
            saveInquiryImages(savedInquiry, images);
            savedInquiry = buyerInquiryRepository.save(savedInquiry);
        }
        return BuyerInquiryResponseDTO.fromEntity(savedInquiry);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<BuyerInquiryListResponseDTO> getBuyerInquiryList(String userId, PageRequestDTO pageRequestDTO) {
        BuyerEntity buyer = buyerRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        Pageable pageable = pageRequestDTO.toPageable();
        Page<BuyerInquiryEntity> page = buyerInquiryRepository.findAll(pageable);
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

    public BuyerInquiryResponseDTO updateBuyerInquiry(Long inquiryId, String userId, BuyerInquiryUpdateRequestDTO dto, List<MultipartFile> newImages) {
        BuyerInquiryEntity inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (!inquiry.getBuyer().getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("문의 작성자만 수정할 수 있습니다.");
        }

        if (inquiry.getInquiryStatus() == BuyerInquiryEntity.InquiryStatus.ANSWERED) {
            throw new IllegalArgumentException("답변이 완료된 문의는 수정할 수 없습니다.");
        }

        inquiry.setInquiryType(dto.getInquiryType());
        inquiry.setTitle(dto.getTitle());
        inquiry.setQuestionContent(dto.getQuestionContent());

        if (dto.getDeletedImageIds() != null && !dto.getDeletedImageIds().isEmpty()) {
            deleteInquiryImages(inquiry, dto.getDeletedImageIds());
        }

        if (newImages != null && !newImages.isEmpty()) {
            saveInquiryImages(inquiry, newImages);
        }

        return BuyerInquiryResponseDTO.fromEntity(buyerInquiryRepository.save(inquiry));
    }

    public void deleteBuyerInquiry(Long inquiryId, String userId) {
        BuyerInquiryEntity inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (!inquiry.getBuyer().getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("문의 작성자만 삭제할 수 있습니다.");
        }

        // Delete associated images from the file system
        inquiry.getImages().forEach(image -> {
            String filePath = uploadPath + inquiry.getBuyer().getBuyerUid() + "/" + inquiry.getId() + "/" + image.getStoredName();
            fileUtils.deleteFile(filePath);
        });

        buyerInquiryRepository.delete(inquiry);
    }

    private void saveInquiryImages(BuyerInquiryEntity inquiry, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            String path = uploadPath + inquiry.getBuyer().getBuyerUid() + "/" + inquiry.getId();
            String storedName = fileUtils.saveFile(file, path);

            BuyerInquiryImageEntity imageEntity = new BuyerInquiryImageEntity();
            imageEntity.setBuyerInquiry(inquiry);
            imageEntity.setUploaderType(BuyerInquiryImageEntity.UploaderType.USER);
            imageEntity.setImageName(file.getOriginalFilename());
            imageEntity.setStoredName(storedName);
            imageEntity.setImagePath("/buyerinquiry/" + inquiry.getBuyer().getBuyerUid() + "/" + inquiry.getId() + "/" + storedName);
            imageEntity.setImageSize(file.getSize());
            inquiry.getImages().add(imageEntity);
        }
    }

    private void deleteInquiryImages(BuyerInquiryEntity inquiry, List<Long> deletedImageIds) {
        inquiry.getImages().removeIf(image -> {
            if (deletedImageIds.contains(image.getId())) {
                String filePath = uploadPath + inquiry.getBuyer().getBuyerUid() + "/" + inquiry.getId() + "/" + image.getStoredName();
                fileUtils.deleteFile(filePath);
                return true;
            }
            return false;
        });
    }
}

