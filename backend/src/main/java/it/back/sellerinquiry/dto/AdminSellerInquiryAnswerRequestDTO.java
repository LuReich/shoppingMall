package it.back.sellerinquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSellerInquiryAnswerRequestDTO {

    @NotBlank
    private String answerContent;
}
