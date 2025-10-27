package it.back.buyer.repository;

import it.back.buyer.entity.BuyerDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerDetailRepository extends JpaRepository<BuyerDetailEntity, Long> {
    Optional<BuyerDetailEntity> findByBuyerUid(Long buyerUid);
}
