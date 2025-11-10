package it.back.sellerinquiry.specification;

import it.back.seller.entity.SellerEntity;
import it.back.sellerinquiry.entity.SellerInquiryEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class SellerInquirySpecification {

    /**
     * 상태(status)로 필터링
     */
    public static Specification<SellerInquiryEntity> hasStatus(SellerInquiryEntity.InquiryStatus inquiryStatus) {
        return (root, query, criteriaBuilder) -> {
            if (inquiryStatus == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("inquiryStatus"), inquiryStatus);
        };
    }

    /**
     * 문의 유형(inquiryType)으로 필터링
     */
    public static Specification<SellerInquiryEntity> hasInquiryType(SellerInquiryEntity.InquiryType inquiryType) {
        return (root, query, criteriaBuilder) -> {
            if (inquiryType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("inquiryType"), inquiryType);
        };
    }

    /**
     * 판매자 UID로 필터링
     */
    public static Specification<SellerInquiryEntity> hasSellerUid(Long sellerUid) {
        return (root, query, criteriaBuilder) -> {
            if (sellerUid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("seller").get("sellerUid"), sellerUid);
        };
    }

    /**
     * 제목 또는 내용에 키워드가 포함되어 있는지 검색
     */
    public static Specification<SellerInquiryEntity> contentContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("questionContent"), "%" + keyword + "%")
            );
        };
    }

    /**
     * 판매자 업체명에 키워드가 포함되어 있는지 검색 (공백 무시 및 AND 조건)
     */
    public static Specification<SellerInquiryEntity> companyNameContains(String companyName) {
        return (root, query, criteriaBuilder) -> {
            if (companyName == null || companyName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<SellerInquiryEntity, SellerEntity> sellerJoin = root.join("seller", JoinType.INNER);

            // DB의 companyName 컬럼에서 공백을 제거하는 표현식
            jakarta.persistence.criteria.Expression<String> companyNameWithoutSpaces = criteriaBuilder.function(
                    "replace",
                    String.class,
                    sellerJoin.get("companyName"),
                    criteriaBuilder.literal(" "),
                    criteriaBuilder.literal("")
            );

            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            // 검색어를 공백 기준으로 분리
            String[] keywords = companyName.trim().split("\s+");

            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    // 공백 제거된 DB값에서 각 키워드가 포함되어 있는지 (LIKE) 확인
                    predicates.add(criteriaBuilder.like(companyNameWithoutSpaces, "%" + keyword + "%"));
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // 모든 키워드를 AND 조건으로 묶음
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
