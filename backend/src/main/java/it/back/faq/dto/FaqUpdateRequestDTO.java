package it.back.faq.dto;

import it.back.faq.entity.FaqEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqUpdateRequestDTO {
    private FaqEntity.FaqTarget faqTarget;
    private FaqEntity.FaqCategory faqCategory;
    private String faqQuestion;
    private String faqAnswer;
    private Integer sortOrder;
}
