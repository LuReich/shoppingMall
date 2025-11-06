package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BuyerInquiryResponseDTO {
    private Long inquiryId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String buyerNickname;
    private List<String> imageUrls;
    private InquiryAnswerResponseDTO answer;

    public BuyerInquiryResponseDTO(BuyerInquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.status = inquiry.getStatus().name();
        this.createdAt = inquiry.getCreateDate();
        this.updatedAt = inquiry.getUpdateDate();
        this.buyerNickname = inquiry.getBuyer().getNickname();
        this.imageUrls = inquiry.getImages().stream()
                .map(image -> image.getImagePath())
                .collect(Collectors.toList());
        if (inquiry.getAnswer() != null) {
            this.answer = new InquiryAnswerResponseDTO(inquiry.getAnswer());
        }
    }
}
