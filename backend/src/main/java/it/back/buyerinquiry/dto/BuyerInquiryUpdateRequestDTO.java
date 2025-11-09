package it.back.buyerinquiry.dto;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuyerInquiryUpdateRequestDTO {

    @NotNull
    private BuyerInquiryEntity.InquiryType inquiryType;

    @NotBlank
    private String title;

    @NotBlank
    private String questionContent;

    private List<Long> deletedImageIds;
}
