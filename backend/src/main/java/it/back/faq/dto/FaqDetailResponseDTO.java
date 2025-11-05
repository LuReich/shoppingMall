package it.back.faq.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class FaqDetailResponseDTO {
    private Integer faqId;
    private String faqTarget;
    private String faqCategory;
    private String faqQuestion;
    private String faqAnswer;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
