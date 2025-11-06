package it.back.admin.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.seller.entity.SellerEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class AdminSellerSpecifications {

    public static Specification<SellerEntity> hasSellerUid(Long sellerUid) {
        return (root, query, criteriaBuilder) -> {
            if (sellerUid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("sellerUid"), sellerUid);
        };
    }

    public static Specification<SellerEntity> hasSellerId(String sellerId) {
        return (root, query, criteriaBuilder) -> {
            if (sellerId == null || sellerId.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("sellerId"), "%" + sellerId + "%");
        };
    }

    public static Specification<SellerEntity> hasCompanyName(String companyName) {
        return (root, query, criteriaBuilder) -> {
            if (companyName == null || companyName.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String[] keywords = companyName.trim().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();
            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("companyName"), "%" + keyword + "%"));
                }
            }
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SellerEntity> hasSellerEmail(String sellerEmail) {
        return (root, query, criteriaBuilder) -> {
            if (sellerEmail == null || sellerEmail.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("sellerEmail"), "%" + sellerEmail + "%");
        };
    }

    public static Specification<SellerEntity> hasPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null || phone.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<SellerEntity, Object> detailJoin = root.join("sellerDetail");
            return criteriaBuilder.like(detailJoin.get("phone"), "%" + phone + "%");
        };
    }

    public static Specification<SellerEntity> hasBusinessRegistrationNumber(String businessRegistrationNumber) {
        return (root, query, criteriaBuilder) -> {
            if (businessRegistrationNumber == null || businessRegistrationNumber.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<SellerEntity, Object> detailJoin = root.join("sellerDetail");
            return criteriaBuilder.like(detailJoin.get("businessRegistrationNumber"), "%" + businessRegistrationNumber + "%");
        };
    }

    public static Specification<SellerEntity> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<SellerEntity> isVerified(Boolean isVerified) {
        return (root, query, criteriaBuilder) -> {
            if (isVerified == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isVerified"), isVerified);
        };
    }

    public static Specification<SellerEntity> hasWithdrawalStatus(String withdrawalStatus) {
        return (root, query, criteriaBuilder) -> {
            if (withdrawalStatus == null || withdrawalStatus.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            try {
                SellerEntity.WithdrawalStatus status = SellerEntity.WithdrawalStatus.valueOf(withdrawalStatus.toUpperCase());
                return criteriaBuilder.equal(root.get("withdrawalStatus"), status);
            } catch (IllegalArgumentException e) {
                return criteriaBuilder.disjunction(); // Always false predicate for invalid enum
            }
        };
    }
}
