package it.back.faq.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqDTO {
    private Integer faqId;
    private Integer adminUid;
    private String faqTarget;
    private String faqCategory;
    private String faqQuestion;
    private String faqAnswer;
    private Integer sortOrder;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
