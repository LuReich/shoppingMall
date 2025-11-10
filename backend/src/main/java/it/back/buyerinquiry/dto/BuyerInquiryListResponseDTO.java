package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerInquiryListResponseDTO {

    private Long inquiryId;
    private BuyerInquiryEntity.InquiryType inquiryType;
    private String title;
    private Long buyerUid;
    private String buyerNickname;
    private BuyerInquiryEntity.InquiryStatus inquiryStatus;
    private LocalDateTime createdAt;

    public static BuyerInquiryListResponseDTO fromEntity(BuyerInquiryEntity entity) {
        return BuyerInquiryListResponseDTO.builder()
                .inquiryId(entity.getInquiryId())
                .inquiryType(entity.getInquiryType())
                .title(entity.getTitle())
                .buyerUid(entity.getBuyer() != null ? entity.getBuyer().getBuyerUid() : null)
                .buyerNickname(entity.getBuyer() != null ? entity.getBuyer().getNickname() : null)
                .inquiryStatus(entity.getInquiryStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
