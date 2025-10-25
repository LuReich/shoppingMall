package it.back.seller.repository;

import it.back.seller.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<SellerEntity, Long> {

    Optional<SellerEntity> findBySellerId(String sellerId);

    Optional<SellerEntity> findBySellerEmail(String sellerEmail);

    Optional<SellerEntity> findBySellerDetail_Phone(String phone);

    Optional<SellerEntity> findBySellerDetail_BusinessRegistrationNumber(String businessRegistrationNumber);
}
