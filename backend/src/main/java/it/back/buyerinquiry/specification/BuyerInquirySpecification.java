package it.back.buyerinquiry.specification;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyerinquiry.entity.BuyerInquiryEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BuyerInquirySpecification {

    /**
     * 상태(status)로 필터링
     */
    public static Specification<BuyerInquiryEntity> hasStatus(BuyerInquiryEntity.InquiryStatus inquiryStatus) {
        return (root, query, criteriaBuilder) -> {
            if (inquiryStatus == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("inquiryStatus"), inquiryStatus);
        };
    }

    /**
     * 구매자 UID로 필터링
     */
    public static Specification<BuyerInquiryEntity> hasBuyerUid(Long buyerUid) {
        return (root, query, criteriaBuilder) -> {
            if (buyerUid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("buyer").get("buyerUid"), buyerUid);
        };
    }

    /**
     * 제목 또는 내용에 키워드가 포함되어 있는지 검색
     */
    public static Specification<BuyerInquiryEntity> contentContains(String keyword) {
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
     * 구매자 닉네임에 키워드가 포함되어 있는지 검색
     */
    public static Specification<BuyerInquiryEntity> nicknameContains(String nickname) {
        return (root, query, criteriaBuilder) -> {
            if (nickname == null || nickname.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<BuyerInquiryEntity, BuyerEntity> buyerJoin = root.join("buyer", JoinType.INNER);
            return criteriaBuilder.like(buyerJoin.get("nickname"), "%" + nickname + "%");
        };
    }
}
