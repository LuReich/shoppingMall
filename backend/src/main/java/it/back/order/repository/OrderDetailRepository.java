package it.back.order.repository;

import it.back.order.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    // 주문(orderId)로 모든 주문상세 조회
    List<OrderDetailEntity> findByOrderOrderId(Long orderId);
}
