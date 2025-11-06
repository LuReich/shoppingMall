package it.back.buyerinquiry.repository;

import it.back.buyerinquiry.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
}
