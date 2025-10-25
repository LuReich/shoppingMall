package it.back.cart.repository;

import it.back.buyer.entity.BuyerEntity;
import it.back.cart.entity.CartEntity;
import it.back.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByBuyerAndProduct(BuyerEntity buyer, ProductEntity product);

    Page<CartEntity> findByBuyer(BuyerEntity buyer, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CartEntity c WHERE c.id IN :ids AND c.buyer = :buyer")
    void deleteByIdInAndBuyer(@Param("ids") List<Long> ids, @Param("buyer") BuyerEntity buyer);
}