package it.back.faq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer sortOrder;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;
}
