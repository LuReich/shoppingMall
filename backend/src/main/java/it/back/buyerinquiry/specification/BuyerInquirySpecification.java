package it.back.buyerinquiry.specification;

import org.springframework.data.jpa.domain.Specification;

import it.back.buyerinquiry.entity.BuyerInquiryEntity;

public class BuyerInquirySpecification {

    public static Specification<BuyerInquiryEntity> hasStatus(BuyerInquiryEntity.InquiryStatus inquiryStatus) {
        return (root, query, criteriaBuilder) -> {
            if (inquiryStatus == null) {
                return criteriaBuilder.conjunction(); // 조건이 없으면 항상 true
            }
            return criteriaBuilder.equal(root.get("inquiryStatus"), inquiryStatus);
        };
    }
}
