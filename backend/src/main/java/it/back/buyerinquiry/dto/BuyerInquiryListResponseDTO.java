package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BuyerInquiryListResponseDTO {
    private Long inquiryId;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private String buyerNickname;
    private boolean hasAnswer;

    public BuyerInquiryListResponseDTO(BuyerInquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.title = inquiry.getTitle();
        this.status = inquiry.getStatus().name();
        this.createdAt = inquiry.getCreateDate();
        this.buyerNickname = inquiry.getBuyer().getNickname();
        this.hasAnswer = inquiry.getAnswer() != null;
    }
}
