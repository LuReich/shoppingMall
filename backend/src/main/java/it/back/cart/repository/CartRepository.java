package it.back.cart.repository;

import it.back.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByBuyer_BuyerUid(Long buyerUid);
    boolean existsByBuyer_BuyerUidAndProduct_ProductId(Long buyerUid, Long productId);
    CartEntity findByBuyer_BuyerUidAndProduct_ProductId(Long buyerUid, Long productId);
}