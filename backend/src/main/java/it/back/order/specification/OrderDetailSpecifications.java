package it.back.order.specification;

import java.util.ArrayList;
import java.util.List;

import it.back.order.entity.OrderDetailEntity;
import it.back.product.entity.ProductEntity;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class OrderDetailSpecifications {

    public static Specification<OrderDetailEntity> hasSellerUid(Long sellerUid) {
        return (root, query, criteriaBuilder) -> {
            if (sellerUid == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("sellerUid"), sellerUid);
        };
    }

    public static Specification<OrderDetailEntity> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<OrderDetailEntity, ProductEntity> productJoin = root.join("product");

            // Expression that removes spaces from the productName column
            jakarta.persistence.criteria.Expression<String> productNameWithoutSpaces = criteriaBuilder.function(
                    "replace",
                    String.class,
                    productJoin.get("productName"),
                    criteriaBuilder.literal(" "),
                    criteriaBuilder.literal("")
            );

            List<Predicate> predicates = new ArrayList<>();
            String[] keywords = productName.trim().split("\\s+");

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

    public static Specification<OrderDetailEntity> hasOrderDetailStatus(String orderDetailStatus) {
        return (root, query, criteriaBuilder) -> {
            if (orderDetailStatus == null || orderDetailStatus.isBlank()) {
                return criteriaBuilder.conjunction(); // Always-true predicate
            }
            try {
                OrderDetailEntity.OrderDetailStatus status = OrderDetailEntity.OrderDetailStatus.valueOf(orderDetailStatus.toUpperCase());
                return criteriaBuilder.equal(root.get("orderDetailStatus"), status);
            } catch (IllegalArgumentException e) {
                return criteriaBuilder.disjunction(); // 유효하지 않은 상태 값은 결과 없음
            }
        };
    }

    public static Specification<OrderDetailEntity> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("productId"), productId);
        };
    }

    public static Specification<OrderDetailEntity> hasCategoryId(Integer categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<OrderDetailEntity, ProductEntity> productJoin = root.join("product");
            return criteriaBuilder.equal(productJoin.get("categoryId"), categoryId);
        };
    }
}