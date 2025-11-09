package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerInquiryCreateRequestDTO {

    @NotNull
    private BuyerInquiryEntity.InquiryType inquiryType;

    @NotBlank
    private String title;

    @NotBlank
    private String questionContent;
}
