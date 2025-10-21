package it.back.buyer.repository;

import it.back.buyer.entity.BuyerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<BuyerEntity, Long> {
    Optional<BuyerEntity> findByBuyerId(String buyerId);
}
