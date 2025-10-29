package it.back.order.repository;

import it.back.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // buyerUid로 주문 목록을 페이지네이션하여 조회
    Page<OrderEntity> findByBuyerUid(Long buyerUid, Pageable pageable);
}
