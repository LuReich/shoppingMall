package it.back.order.repository;

import it.back.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // 추가 쿼리 메서드는 필요시 작성
}
