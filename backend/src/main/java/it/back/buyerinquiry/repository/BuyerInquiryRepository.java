package it.back.buyerinquiry.repository;

import it.back.buyerinquiry.entity.BuyerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyerInquiryRepository extends JpaRepository<BuyerInquiry, Long> {
    List<BuyerInquiry> findByBuyer_BuyerUid(Long buyerUid);

    @Query("SELECT bi FROM BuyerInquiry bi LEFT JOIN FETCH bi.images LEFT JOIN FETCH bi.answer WHERE bi.inquiryId = :inquiryId")
    Optional<BuyerInquiry> findByIdWithDetails(@Param("inquiryId") Long inquiryId);
}
