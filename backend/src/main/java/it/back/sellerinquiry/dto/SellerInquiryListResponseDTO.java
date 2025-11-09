package it.back.sellerinquiry.dto;

import it.back.sellerinquiry.entity.SellerInquiryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerInquiryListResponseDTO {

    private Long inquiryId;
    private String title;
    private Long sellerUid;
    private String sellerCompanyName;
    private SellerInquiryEntity.InquiryStatus inquiryStatus;
    private LocalDateTime createdAt;

    public static SellerInquiryListResponseDTO fromEntity(SellerInquiryEntity entity) {
        return SellerInquiryListResponseDTO.builder()
                .inquiryId(entity.getId())
                .title(entity.getTitle())
                .sellerUid(entity.getSeller() != null ? entity.getSeller().getSellerUid() : null)
                .sellerCompanyName(entity.getSeller() != null ? entity.getSeller().getCompanyName() : null)
                .inquiryStatus(entity.getInquiryStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
