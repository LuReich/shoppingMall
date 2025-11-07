package it.back.buyerinquiry.repository;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BuyerInquiryRepository extends JpaRepository<BuyerInquiryEntity, Long>, JpaSpecificationExecutor<BuyerInquiryEntity> {
}
