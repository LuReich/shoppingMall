package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.InquiryAnswer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InquiryAnswerResponseDTO {
    private Long answerId;
    private String content;
    private LocalDateTime createdAt;
    private String adminNickname;

    public InquiryAnswerResponseDTO(InquiryAnswer answer) {
        this.answerId = answer.getAnswerId();
        this.content = answer.getContent();
        this.createdAt = answer.getCreateDate();
        this.adminNickname = answer.getAdmin().getAdminName();
    }
}
