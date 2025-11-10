package it.back.admin.specification;

import it.back.admin.dto.AdminOrderSearchDTO;
import it.back.order.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminOrderSpecification {

    public static Specification<OrderEntity> withFilters(AdminOrderSearchDTO filter) {
        Specification<OrderEntity> spec = (root, query, criteriaBuilder) -> null;

        if (filter.getOrderId() != null) {
            spec = spec.and(hasOrderId(filter.getOrderId()));
        }
        if (filter.getBuyerUid() != null) {
            spec = spec.and(hasBuyerUid(filter.getBuyerUid()));
        }
        if (StringUtils.hasText(filter.getRecipientName())) {
            spec = spec.and(likeRecipientName(filter.getRecipientName()));
        }
        if (StringUtils.hasText(filter.getRecipientAddress())) {
            spec = spec.and(likeRecipientAddress(filter.getRecipientAddress()));
        }
        if (StringUtils.hasText(filter.getRecipientAddressDetail())) {
            spec = spec.and(likeRecipientAddressDetail(filter.getRecipientAddressDetail()));
        }
        if (StringUtils.hasText(filter.getOrderStatus())) {
            spec = spec.and(hasStatus(filter.getOrderStatus()));
        }
        if (StringUtils.hasText(filter.getStartDate()) && StringUtils.hasText(filter.getEndDate())) {
            spec = spec.and(isBetweenDates(filter.getStartDate(), filter.getEndDate()));
        }

        return spec;
    }

    private static Specification<OrderEntity> hasOrderId(Long orderId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderId"), orderId);
    }

    private static Specification<OrderEntity> hasBuyerUid(Long buyerUid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("buyerUid"), buyerUid);
    }

    private static Specification<OrderEntity> likeRecipientName(String recipientName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("recipientName"), "%" + recipientName + "%");
    }

    private static Specification<OrderEntity> likeRecipientAddress(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("recipientAddress"), "%" + address + "%");
    }

    private static Specification<OrderEntity> likeRecipientAddressDetail(String addressDetail) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("recipientAddressDetail"), "%" + addressDetail + "%");
    }

    private static Specification<OrderEntity> hasStatus(String status) {
        try {
            OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status.toUpperCase());
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderStatus"), orderStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Specification<OrderEntity> isBetweenDates(String startDateStr, String endDateStr) {
        try {
            LocalDateTime startDateTime = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDateStr).atTime(LocalTime.MAX);
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createAt"), startDateTime, endDateTime);
        } catch (Exception e) {
            return null;
        }
    }
}
