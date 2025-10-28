package it.back.product.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.product.entity.ProductEntity;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecifications {

    public static Specification<ProductEntity> nameContains(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.isBlank()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }

            List<Predicate> predicates = new ArrayList<>();
            String[] keywords = productName.trim().split("\\s+");

            for (String keyword : keywords) {
                if (!keyword.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("productName"), "%" + keyword + "%"));
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
            return root.get("category").get("categoryId").in(categoryIds);
        };
    }
}
