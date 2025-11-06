
package it.back.admin.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.buyer.entity.BuyerEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class AdminBuyerSpecification {

    public static Specification<BuyerEntity> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<BuyerEntity> hasBuyerUid(Long buyerUid) {
        return (root, query, criteriaBuilder) -> {
            if (buyerUid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("buyerUid"), buyerUid);
        };
    }

    public static Specification<BuyerEntity> hasBuyerId(String buyerId) {
        return (root, query, criteriaBuilder) -> {
            if (buyerId == null || buyerId.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("buyerId"), "%" + buyerId + "%");
        };
    }

    public static Specification<BuyerEntity> hasNickname(String nickname) {
        return (root, query, criteriaBuilder) -> {
            if (nickname == null || nickname.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String[] keywords = nickname.trim().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();
            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + keyword + "%"));
                }
            }
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<BuyerEntity> hasBuyerEmail(String buyerEmail) {
        return (root, query, criteriaBuilder) -> {
            if (buyerEmail == null || buyerEmail.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("buyerEmail"), "%" + buyerEmail + "%");
        };
    }

    public static Specification<BuyerEntity> hasPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null || phone.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<BuyerEntity, Object> detailJoin = root.join("buyerDetail");
            return criteriaBuilder.like(detailJoin.get("phone"), "%" + phone + "%");
        };
    }

    public static Specification<BuyerEntity> hasWithdrawalStatus(String withdrawalStatus) {
        return (root, query, criteriaBuilder) -> {
            if (withdrawalStatus == null || withdrawalStatus.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            try {
                BuyerEntity.WithdrawalStatus status = BuyerEntity.WithdrawalStatus.valueOf(withdrawalStatus.toUpperCase());
                return criteriaBuilder.equal(root.get("withdrawalStatus"), status);
            }
            catch (IllegalArgumentException e) {
                return criteriaBuilder.disjunction(); // Always false predicate for invalid enum
            }
        };
    }
}
