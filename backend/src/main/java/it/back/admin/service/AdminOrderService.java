package it.back.admin.service;

import it.back.admin.dto.AdminOrderDetailResponseDTO;
import it.back.admin.dto.AdminOrderDetailSearchDTO;
import it.back.admin.dto.AdminOrderResponseDTO;
import it.back.admin.dto.AdminOrderSearchDTO;
import it.back.admin.specification.AdminOrderDetailSpecification;
import it.back.admin.specification.AdminOrderSpecification;
import it.back.common.pagination.PageResponseDTO;
import it.back.order.entity.OrderDetailEntity;
import it.back.order.entity.OrderEntity;
import it.back.order.repository.OrderDetailRepository;
import it.back.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public PageResponseDTO<AdminOrderResponseDTO> searchOrders(AdminOrderSearchDTO searchDTO) {
        if (searchDTO.getSort() == null || searchDTO.getSort().isEmpty()) {
            searchDTO.setSort(List.of("createAt,desc"));
        }
        Pageable pageable = searchDTO.toPageable();
        Specification<OrderEntity> spec = AdminOrderSpecification.withFilters(searchDTO);

        Page<OrderEntity> resultPage = orderRepository.findAll(spec, pageable);
        Page<AdminOrderResponseDTO> dtoPage = resultPage.map(AdminOrderResponseDTO::fromEntity);

        return new PageResponseDTO<>(dtoPage);
    }

    public PageResponseDTO<AdminOrderDetailResponseDTO> searchOrderDetails(AdminOrderDetailSearchDTO searchDTO) {
        if (searchDTO.getSort() == null || searchDTO.getSort().isEmpty()) {
            searchDTO.setSort(List.of("createAt,desc"));
        }
        Pageable pageable = searchDTO.toPageable();
        Specification<OrderDetailEntity> spec = AdminOrderDetailSpecification.withFilters(searchDTO);

        Page<OrderDetailEntity> resultPage = orderDetailRepository.findAll(spec, pageable);
        Page<AdminOrderDetailResponseDTO> dtoPage = resultPage.map(AdminOrderDetailResponseDTO::fromEntity);

        return new PageResponseDTO<>(dtoPage);
    }

    public AdminOrderResponseDTO getOrderById(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        return AdminOrderResponseDTO.fromEntity(orderEntity);
    }

    public AdminOrderDetailResponseDTO getOrderDetailById(Long orderDetailId) {
        OrderDetailEntity orderDetailEntity = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Order Detail not found with ID: " + orderDetailId));
        return AdminOrderDetailResponseDTO.fromEntity(orderDetailEntity);
    }
}
