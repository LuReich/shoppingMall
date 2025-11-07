package it.back.buyerinquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBuyerInquiryAnswerRequestDTO {

    @NotBlank
    private String answerContent;
}
