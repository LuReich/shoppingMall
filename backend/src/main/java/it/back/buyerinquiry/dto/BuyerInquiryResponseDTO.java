package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;
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
public class BuyerInquiryResponseDTO {

    private Long id;
    private String buyerNickname;
    private String adminName;
    private BuyerInquiryEntity.InquiryType inquiryType;
    private String title;
    private String questionContent;
    private String answerContent;
    private BuyerInquiryEntity.InquiryStatus inquiryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime answerAt;
    private List<ImageInfo> images;

    public static BuyerInquiryResponseDTO fromEntity(BuyerInquiryEntity entity) {
        return BuyerInquiryResponseDTO.builder()
                .id(entity.getId())
                .buyerNickname(entity.getBuyer() != null ? entity.getBuyer().getNickname() : null)
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
        private Long id;
        private String imagePath;

        public static ImageInfo fromEntity(it.back.buyerinquiry.entity.BuyerInquiryImageEntity imageEntity) {
            return ImageInfo.builder()
                    .id(imageEntity.getId())
                    .imagePath(imageEntity.getImagePath())
                    .build();
        }
    }
}
