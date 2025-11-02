package it.back.product.repository;

import it.back.product.entity.ProductLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLikeEntity, Long> {

    Optional<ProductLikeEntity> findByBuyer_BuyerUidAndProduct_ProductId(Long buyerUid, Long productId);

    long countByProduct_ProductId(Long productId);

    @Query("SELECT pl FROM ProductLikeEntity pl JOIN FETCH pl.product p JOIN FETCH p.seller " +
            "WHERE pl.buyer.buyerUid = :buyerUid " +
            "AND (:productName IS NULL OR p.productName LIKE %:productName%) " +
            "AND (:companyName IS NULL OR p.seller.companyName LIKE %:companyName%) " +
            "AND (:productId IS NULL OR p.productId = :productId)")
    Page<ProductLikeEntity> findLikedProductsByBuyer(@Param("buyerUid") Long buyerUid,
                                                      @Param("productName") String productName,
                                                      @Param("companyName") String companyName,
                                                      @Param("productId") Long productId,
                                                      Pageable pageable);
}
