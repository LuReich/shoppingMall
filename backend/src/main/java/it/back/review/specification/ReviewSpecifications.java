package it.back.review.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.review.entity.ReviewEntity;
import jakarta.persistence.criteria.Predicate;

public class ReviewSpecifications {

    public static Specification<ReviewEntity> hasBuyerId(Long buyerUid) {
        return (root, query, criteriaBuilder) -> {
            if (buyerUid == null) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }
            return criteriaBuilder.equal(root.get("buyer").get("buyerUid"), buyerUid);
        };
    }

    public static Specification<ReviewEntity> productNameContains(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.isBlank()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }

            // Expression that removes spaces from the productName column
            jakarta.persistence.criteria.Expression<String> productNameWithoutSpaces = criteriaBuilder.function(
                "replace", 
                String.class, 
                root.get("product").get("productName"), 
                criteriaBuilder.literal(" "), 
                criteriaBuilder.literal("")
            );

            List<Predicate> predicates = new ArrayList<>();
            String[] keywords = productName.trim().split("\s+");

            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    predicates.add(criteriaBuilder.like(productNameWithoutSpaces, "%" + keyword + "%"));
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
