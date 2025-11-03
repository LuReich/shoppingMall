package it.back.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Added this

import it.back.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productImages WHERE p.productId = :id")
    Optional<ProductEntity> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT p FROM ProductEntity p WHERE REPLACE(p.productName, ' ', '') LIKE %:name%")
    Page<ProductEntity> findByProductNameIgnoreSpace(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.categoryId IN :categoryIds AND REPLACE(p.productName, ' ', '') LIKE %:name%")
    Page<ProductEntity> findByCategoryIdAndProductNameIgnoreSpace(@Param("categoryIds") List<Integer> categoryIds, @Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.categoryId IN :categoryIds")
    Page<ProductEntity> findByCategoryId(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);

    // 판매자별 상품 목록 조회 (Seller 정보 포함)
    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.seller WHERE p.seller.sellerUid = :sellerUid")
    Page<ProductEntity> findBySellerUid(@Param("sellerUid") Long sellerUid, Pageable pageable);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.likeCount = p.likeCount + :amount WHERE p.productId = :productId")
    void updateLikeCount(@Param("productId") Long productId, @Param("amount") int amount);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.averageRating = :averageRating WHERE p.productId = :productId")
    void updateAverageRating(@Param("productId") Long productId, @Param("averageRating") double averageRating);
}
