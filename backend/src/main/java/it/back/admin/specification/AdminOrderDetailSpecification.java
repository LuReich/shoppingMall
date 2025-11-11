package it.back.admin.specification;

import it.back.admin.dto.AdminOrderDetailSearchDTO;
import it.back.order.entity.OrderDetailEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminOrderDetailSpecification {

    public static Specification<OrderDetailEntity> withFilters(AdminOrderDetailSearchDTO filter) {
        Specification<OrderDetailEntity> spec = (root, query, criteriaBuilder) -> null;

        if (filter.getOrderDetailId() != null) {
            spec = spec.and(hasOrderDetailId(filter.getOrderDetailId()));
        }
        if (filter.getOrderId() != null) {
            spec = spec.and(hasOrderId(filter.getOrderId()));
        }
        if (filter.getProductId() != null) {
            spec = spec.and(hasProductId(filter.getProductId()));
        }
        if (filter.getSellerUid() != null) {
            spec = spec.and(hasSellerUid(filter.getSellerUid()));
        }
        if (StringUtils.hasText(filter.getOrderDetailStatus())) {
            spec = spec.and(hasStatus(filter.getOrderDetailStatus()));
        }
        if (StringUtils.hasText(filter.getStartDate()) && StringUtils.hasText(filter.getEndDate())) {
            spec = spec.and(isBetweenDates(filter.getStartDate(), filter.getEndDate()));
        }

        return spec;
    }

    private static Specification<OrderDetailEntity> hasOrderDetailId(Long orderDetailId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderDetailId"), orderDetailId);
    }

    private static Specification<OrderDetailEntity> hasOrderId(Long orderId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("order").get("orderId"), orderId);
    }

    private static Specification<OrderDetailEntity> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("productId"), productId);
    }

    private static Specification<OrderDetailEntity> hasSellerUid(Long sellerUid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sellerUid"), sellerUid);
    }

    private static Specification<OrderDetailEntity> hasStatus(String status) {
        try {
            OrderDetailEntity.OrderDetailStatus orderDetailStatus = OrderDetailEntity.OrderDetailStatus.valueOf(status.toUpperCase());
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderDetailStatus"), orderDetailStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Specification<OrderDetailEntity> isBetweenDates(String startDateStr, String endDateStr) {
        try {
            LocalDateTime startDateTime = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDateStr).atTime(LocalTime.MAX);
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createAt"), startDateTime, endDateTime);
        } catch (Exception e) {
            return null;
        }
    }
}
