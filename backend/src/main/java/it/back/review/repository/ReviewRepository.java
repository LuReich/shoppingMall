package it.back.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.back.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {

    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.buyer JOIN FETCH r.product p JOIN FETCH p.seller WHERE p.productId = :productId")
    Page<ReviewEntity> findAllByProductIdWithBuyerAndSellerPaged(@Param("productId") Long productId, Pageable pageable);

    List<ReviewEntity> findByProduct_ProductId(Long productId);

    @Query("SELECT r FROM ReviewEntity r "
            + "JOIN FETCH r.buyer "
            + "JOIN FETCH r.product p "
            + "JOIN FETCH p.seller "
            + "WHERE p.productId = :productId")
    List<ReviewEntity> findAllByProductIdWithBuyerAndSeller(@Param("productId") Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ReviewEntity r WHERE r.product.productId = :productId")
    Double calculateAverageRating(@Param("productId") Long productId);
}
