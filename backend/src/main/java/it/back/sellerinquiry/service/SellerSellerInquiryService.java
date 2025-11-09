package it.back.sellerinquiry.service;

import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.sellerinquiry.dto.SellerInquiryCreateRequestDTO;
import it.back.sellerinquiry.dto.SellerInquiryListResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryResponseDTO;
import it.back.sellerinquiry.dto.SellerInquiryUpdateRequestDTO;
import it.back.sellerinquiry.entity.SellerInquiryEntity;
import it.back.sellerinquiry.entity.SellerInquiryImageEntity;
import it.back.sellerinquiry.repository.SellerInquiryRepository;
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
public class SellerSellerInquiryService {

    private final SellerInquiryRepository sellerInquiryRepository;
    private final SellerRepository sellerRepository;
    private final FileUtils fileUtils;

    private final String uploadPath = "C:/ourshop/sellerinquiry/";

    public SellerInquiryResponseDTO createSellerInquiry(String userId, SellerInquiryCreateRequestDTO dto, List<MultipartFile> images) {
        SellerEntity seller = sellerRepository.findBySellerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        SellerInquiryEntity inquiry = new SellerInquiryEntity();
        inquiry.setSeller(seller);
        inquiry.setInquiryType(dto.getInquiryType());
        inquiry.setTitle(dto.getTitle());
        inquiry.setQuestionContent(dto.getQuestionContent());
        inquiry.setInquiryStatus(SellerInquiryEntity.InquiryStatus.PENDING);

        SellerInquiryEntity savedInquiry = sellerInquiryRepository.save(inquiry);

        if (images != null && !images.isEmpty()) {
            saveInquiryImages(savedInquiry, images);
            savedInquiry = sellerInquiryRepository.save(savedInquiry);
        }
        return SellerInquiryResponseDTO.fromEntity(savedInquiry);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<SellerInquiryListResponseDTO> getSellerInquiryList(String userId, PageRequestDTO pageRequestDTO) {
        SellerEntity seller = sellerRepository.findBySellerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        Pageable pageable = pageRequestDTO.toPageable();
        Page<SellerInquiryEntity> page = sellerInquiryRepository.findBySeller(seller, pageable);
        List<SellerInquiryListResponseDTO> dtoList = page.getContent().stream()
                .map(SellerInquiryListResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(page, dtoList);
    }

    @Transactional(readOnly = true)
    public SellerInquiryResponseDTO getSellerInquiry(Long inquiryId) {
        SellerInquiryEntity inquiry = sellerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));
        return SellerInquiryResponseDTO.fromEntity(inquiry);
    }

    public SellerInquiryResponseDTO updateSellerInquiry(Long inquiryId, String userId, SellerInquiryUpdateRequestDTO dto, List<MultipartFile> newImages) {
        SellerInquiryEntity inquiry = sellerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (!inquiry.getSeller().getSellerId().equals(userId)) {
            throw new IllegalArgumentException("문의 작성자만 수정할 수 있습니다.");
        }

        if (inquiry.getInquiryStatus() == SellerInquiryEntity.InquiryStatus.ANSWERED) {
            throw new IllegalArgumentException("답변이 완료된 문의는 수정할 수 없습니다.");
        }

        inquiry.setInquiryType(dto.getInquiryType());
        inquiry.setTitle(dto.getTitle());
        inquiry.setQuestionContent(dto.getQuestionContent());

        if (dto.getDeletedImageIds() != null && !dto.getDeletedImageIds().isEmpty()) {
            deleteInquiryImages(inquiry, dto.getDeletedImageIds());
        }

        boolean addingNewImages = newImages != null && !newImages.isEmpty();
        if (!addingNewImages && inquiry.getImages().isEmpty()) {
            String directoryPath = uploadPath + inquiry.getSeller().getSellerUid() + "/" + inquiry.getId();
            fileUtils.deleteDirectory(directoryPath);
        }

        if (addingNewImages) {
            saveInquiryImages(inquiry, newImages);
        }

        return SellerInquiryResponseDTO.fromEntity(sellerInquiryRepository.save(inquiry));
    }

    public void deleteSellerInquiry(Long inquiryId, String userId) {
        SellerInquiryEntity inquiry = sellerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (!inquiry.getSeller().getSellerId().equals(userId)) {
            throw new IllegalArgumentException("문의 작성자만 삭제할 수 있습니다.");
        }

        String directoryPath = uploadPath + inquiry.getSeller().getSellerUid() + "/" + inquiry.getId();

        inquiry.getImages().forEach(image -> {
            String filePath = directoryPath + "/" + image.getStoredName();
            fileUtils.deleteFile(filePath);
        });

        fileUtils.deleteDirectory(directoryPath);

        sellerInquiryRepository.delete(inquiry);
    }

    private void saveInquiryImages(SellerInquiryEntity inquiry, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            String path = uploadPath + inquiry.getSeller().getSellerUid() + "/" + inquiry.getId();
            String storedName = fileUtils.saveFile(file, path);

            SellerInquiryImageEntity imageEntity = new SellerInquiryImageEntity();
            imageEntity.setSellerInquiry(inquiry);
            imageEntity.setUploaderType(SellerInquiryImageEntity.UploaderType.USER);
            imageEntity.setImageName(file.getOriginalFilename());
            imageEntity.setStoredName(storedName);
            imageEntity.setImagePath("/sellerinquiry/" + inquiry.getSeller().getSellerUid() + "/" + inquiry.getId() + "/" + storedName);
            imageEntity.setImageSize(file.getSize());
            inquiry.getImages().add(imageEntity);
        }
    }

    private void deleteInquiryImages(SellerInquiryEntity inquiry, List<Long> deletedImageIds) {
        inquiry.getImages().removeIf(image -> {
            if (deletedImageIds.contains(image.getId())) {
                String filePath = uploadPath + inquiry.getSeller().getSellerUid() + "/" + inquiry.getId() + "/" + image.getStoredName();
                fileUtils.deleteFile(filePath);
                return true;
            }
            return false;
        });
    }
}
