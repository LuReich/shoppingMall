package it.back.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.back.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.buyer JOIN FETCH r.product p JOIN FETCH p.seller WHERE p.productId = :productId")
    org.springframework.data.domain.Page<ReviewEntity> findAllByProductIdWithBuyerAndSellerPaged(@Param("productId") Long productId, org.springframework.data.domain.Pageable pageable);

    List<ReviewEntity> findByProduct_ProductId(Long productId);

    @Query("SELECT r FROM ReviewEntity r "
            + "JOIN FETCH r.buyer "
            + "JOIN FETCH r.product p "
            + "JOIN FETCH p.seller "
            + "WHERE p.productId = :productId")
    List<ReviewEntity> findAllByProductIdWithBuyerAndSeller(@Param("productId") Long productId);
}
