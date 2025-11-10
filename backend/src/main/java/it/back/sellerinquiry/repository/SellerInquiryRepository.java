package it.back.sellerinquiry.repository;

import it.back.seller.entity.SellerEntity;
import it.back.sellerinquiry.entity.SellerInquiryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface SellerInquiryRepository extends JpaRepository<SellerInquiryEntity, Long>, JpaSpecificationExecutor<SellerInquiryEntity> {
    Page<SellerInquiryEntity> findBySeller(SellerEntity seller, Pageable pageable);
    Optional<SellerInquiryEntity> findByInquiryId(Long inquiryId);
}
