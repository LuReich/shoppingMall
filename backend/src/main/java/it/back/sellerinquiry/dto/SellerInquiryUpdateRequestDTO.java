package it.back.sellerinquiry.dto;

import it.back.sellerinquiry.entity.SellerInquiryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SellerInquiryUpdateRequestDTO {

    @NotNull
    private SellerInquiryEntity.InquiryType inquiryType;

    @NotBlank
    private String title;

    @NotBlank
    private String questionContent;

    private List<Long> deletedImageIds;
}
