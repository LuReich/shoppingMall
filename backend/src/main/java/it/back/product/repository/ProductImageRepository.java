package it.back.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.back.product.entity.ProductImageEntity;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    // 필요에 따라 특정 상품의 모든 이미지를 조회하는 등의 커스텀 쿼리 메서드를 추가할 수 있습니다.
    // List<ProductImageEntity> findByProductProductId(Long productId);
}
