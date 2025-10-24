    // 잘못된 위치의 메서드 선언 삭제 (클래스 내부에 이미 올바르게 존재)
package it.back.order.service;

import it.back.order.entity.OrderEntity;
import it.back.order.entity.OrderDetailEntity;
import it.back.order.repository.OrderRepository;
import it.back.order.repository.OrderDetailRepository;
import it.back.order.dto.OrderDTO;
import it.back.order.dto.OrderDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) {
        OrderEntity order = new OrderEntity();
        order.setBuyerUid(orderDTO.getBuyerUid());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setRecipientName(orderDTO.getRecipientName());
        order.setRecipientAddress(orderDTO.getRecipientAddress());
        order.setRecipientAddressDetail(orderDTO.getRecipientAddressDetail());
        order.setOrderStatus(OrderEntity.OrderStatus.valueOf(orderDTO.getOrderStatus()));

        if (orderDTO.getOrderDetails() != null) {
            List<OrderDetailEntity> details = orderDTO.getOrderDetails().stream().map(detailDTO -> {
                OrderDetailEntity detail = new OrderDetailEntity();
                detail.setProductId(detailDTO.getProductId());
                detail.setSellerUid(detailDTO.getSellerUid());
                detail.setQuantity(detailDTO.getQuantity());
                detail.setPricePerItem(detailDTO.getPricePerItem());
                detail.setOrderDetailStatus(OrderDetailEntity.OrderDetailStatus.valueOf(detailDTO.getOrderDetailStatus()));
                detail.setOrder(order);
                return detail;
            }).collect(Collectors.toList());
            order.setOrderDetails(details);
        }
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByBuyerUid(Long buyerUid) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getBuyerUid().equals(buyerUid))
                .map(OrderDTO::from)
                .collect(Collectors.toList());
    }

    // toDTO, toDetailDTO 제거: 변환 책임을 DTO로 이동

    /**
     * 주문상세 전체가 DELIVERED면 주문도 DELIVERED로 상태 변경
     * 판매자가 주문 상태 변경 시 자동으로 체크
     */
    @Transactional
    public void updateOrderStatusIfAllDelivered(Long orderId) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(orderId);
        boolean allDelivered = details.stream()
                .allMatch(d -> d.getOrderDetailStatus() == OrderDetailEntity.OrderDetailStatus.DELIVERED);
        if (allDelivered && !details.isEmpty()) {
            OrderEntity order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getOrderStatus() != OrderEntity.OrderStatus.DELIVERED) {
                order.setOrderStatus(OrderEntity.OrderStatus.DELIVERED);
                orderRepository.save(order);
            }
        }
    }
}
