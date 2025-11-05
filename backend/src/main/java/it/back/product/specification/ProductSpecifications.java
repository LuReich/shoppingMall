package it.back.product.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.product.entity.ProductEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecifications {

    public static Specification<ProductEntity> nameContains(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.isBlank()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }

            // Expression that removes spaces from the productName column
            jakarta.persistence.criteria.Expression<String> productNameWithoutSpaces = criteriaBuilder.function(
                    "replace",
                    String.class,
                    root.get("productName"),
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

    public static Specification<ProductEntity> inCategory(List<Integer> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }
            return root.get("categoryId").in(categoryIds);
        };
    }

    public static Specification<ProductEntity> withSeller() {
        return (root, query, criteriaBuilder) -> {
            // Ensure the seller is fetched along with the product to avoid N+1 queries
            // Only apply fetch if it's not a count query
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("seller", JoinType.LEFT);
            }
            return criteriaBuilder.conjunction(); // Always-true predicate
        };
    }

    public static Specification<ProductEntity> companyNameContains(String companyName) {
        return (root, query, criteriaBuilder) -> {
            if (companyName == null || companyName.isBlank()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }

            // Expression that removes spaces from the companyName column
            Expression<String> companyNameWithoutSpaces = criteriaBuilder.function(
                    "replace",
                    String.class,
                    root.get("seller").get("companyName"),
                    criteriaBuilder.literal(" "),
                    criteriaBuilder.literal("")
            );

            List<Predicate> predicates = new ArrayList<>();
            String[] keywords = companyName.trim().split("\s+");

            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    predicates.add(criteriaBuilder.like(companyNameWithoutSpaces, "%" + keyword + "%"));
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductEntity> productIdEquals(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }
            return criteriaBuilder.equal(root.get("productId"), productId);
        };
    }

    public static Specification<ProductEntity> isDeleted(Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> {
            if (isDeleted == null) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }
            return criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
        };
    }
}
