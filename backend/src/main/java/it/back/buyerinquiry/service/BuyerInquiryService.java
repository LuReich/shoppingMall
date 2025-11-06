package it.back.buyerinquiry.service;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.buyerinquiry.dto.BuyerInquiryCreateRequestDTO;
import it.back.buyerinquiry.dto.BuyerInquiryListResponseDTO;
import it.back.buyerinquiry.dto.BuyerInquiryResponseDTO;
import it.back.buyerinquiry.entity.BuyerInquiry;
import it.back.buyerinquiry.entity.InquiryImage;
import it.back.buyerinquiry.entity.InquiryStatus;
import it.back.buyerinquiry.repository.BuyerInquiryRepository;
import it.back.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerInquiryService {

    private final BuyerInquiryRepository buyerInquiryRepository;
    private final BuyerRepository buyerRepository;
    private final FileUtils fileUtils;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getOsIndependentPath(String... paths) {
        return String.join(File.separator, paths);
    }

    @Transactional
    public BuyerInquiryResponseDTO createInquiry(Long buyerUid, BuyerInquiryCreateRequestDTO dto, List<MultipartFile> images) {
        BuyerEntity buyer = buyerRepository.findById(buyerUid)
                .orElseThrow(() -> new IllegalArgumentException("구매자 정보를 찾을 수 없습니다."));

        BuyerInquiry inquiry = BuyerInquiry.builder()
                .buyer(buyer)
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(InquiryStatus.PENDING)
                .build();

        BuyerInquiry savedInquiry = buyerInquiryRepository.save(inquiry);
        Long inquiryId = savedInquiry.getInquiryId();

        if (images != null && !images.isEmpty()) {
            String inquiryPath = getOsIndependentPath(uploadDir, "buyerinquiry", String.valueOf(inquiryId));
            try {
                Files.createDirectories(Paths.get(inquiryPath));
            } catch (IOException e) {
                throw new java.io.UncheckedIOException("문의 이미지 폴더 생성에 실패했습니다.", e);
            }

            List<InquiryImage> imageEntities = new ArrayList<>();
            for (MultipartFile imageFile : images) {
                if (imageFile.isEmpty()) continue;

                String storedName = fileUtils.saveFile(imageFile, inquiryPath);
                String imageUrl = "/buyerinquiry/" + inquiryId + "/" + storedName;

                InquiryImage imageEntity = InquiryImage.builder()
                        .buyerInquiry(savedInquiry)
                        .imagePath(imageUrl)
                        .build();
                imageEntities.add(imageEntity);
            }
            savedInquiry.setImages(imageEntities);
        }

        return new BuyerInquiryResponseDTO(savedInquiry);
    }

    @Transactional(readOnly = true)
    public List<BuyerInquiryListResponseDTO> getInquiriesByBuyer(Long buyerUid) {
        return buyerInquiryRepository.findByBuyer_BuyerUid(buyerUid).stream()
                .map(BuyerInquiryListResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuyerInquiryResponseDTO getInquiryById(Long inquiryId, Long buyerUid) {
        BuyerInquiry inquiry = buyerInquiryRepository.findByIdWithDetails(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        if (!inquiry.getBuyer().getBuyerUid().equals(buyerUid)) {
            throw new SecurityException("자신의 문의만 조회할 수 있습니다.");
        }
        return new BuyerInquiryResponseDTO(inquiry);
    }
}
