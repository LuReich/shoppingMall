package it.back.review.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private Long reviewId;
    private String content;
    private int rating;
    private LocalDateTime createAt;
    private String writer;
}