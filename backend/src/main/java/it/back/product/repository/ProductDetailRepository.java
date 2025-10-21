package it.back.product.repository;

import it.back.product.entity.ProductDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetailEntity, Long> {
}
