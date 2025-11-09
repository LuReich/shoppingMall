package it.back.buyerinquiry.repository;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BuyerInquiryRepository extends JpaRepository<BuyerInquiryEntity, Long>, JpaSpecificationExecutor<BuyerInquiryEntity> {
    Page<BuyerInquiryEntity> findByBuyer(BuyerEntity buyer, Pageable pageable);
}
