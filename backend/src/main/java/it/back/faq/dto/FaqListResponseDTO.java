package it.back.faq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqListResponseDTO {
    private Integer faqId;
    private String faqTarget;
    private String faqCategory;
    private String faqQuestion;
}
