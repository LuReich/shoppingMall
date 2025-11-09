package it.back.sellerinquiry.dto;

import it.back.sellerinquiry.entity.SellerInquiryEntity;
import it.back.sellerinquiry.entity.SellerInquiryImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerInquiryResponseDTO {

    private Long inquiryId;
    private String sellerCompanyName;
    private String adminName;
    private SellerInquiryEntity.InquiryType inquiryType;
    private String title;
    private String questionContent;
    private String answerContent;
    private SellerInquiryEntity.InquiryStatus inquiryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime answerAt;
    private List<ImageInfo> images;

    public static SellerInquiryResponseDTO fromEntity(SellerInquiryEntity entity) {
        return SellerInquiryResponseDTO.builder()
                .inquiryId(entity.getId())
                .sellerCompanyName(entity.getSeller() != null ? entity.getSeller().getCompanyName() : null)
                .adminName(entity.getAdmin() != null ? entity.getAdmin().getAdminName() : null)
                .inquiryType(entity.getInquiryType())
                .title(entity.getTitle())
                .questionContent(entity.getQuestionContent())
                .answerContent(entity.getAnswerContent())
                .inquiryStatus(entity.getInquiryStatus())
                .createdAt(entity.getCreatedAt())
                .answerAt(entity.getAnswerAt())
                .images(entity.getImages().stream().map(ImageInfo::fromEntity).collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfo {
        private Long imageId;
        private Long inquiryId;
        private SellerInquiryImageEntity.UploaderType uploaderType;
        private String imageName;
        private String storedName;
        private String imagePath;
        private Long imageSize;
        private LocalDateTime createdAt;

        public static ImageInfo fromEntity(SellerInquiryImageEntity imageEntity) {
            return ImageInfo.builder()
                    .imageId(imageEntity.getId())
                    .inquiryId(imageEntity.getSellerInquiry().getId())
                    .uploaderType(imageEntity.getUploaderType())
                    .imageName(imageEntity.getImageName())
                    .storedName(imageEntity.getStoredName())
                    .imagePath(imageEntity.getImagePath())
                    .imageSize(imageEntity.getImageSize())
                    .createdAt(imageEntity.getCreatedAt())
                    .build();
        }
    }
}
