package it.back.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Added this

import it.back.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p WHERE p.category.categoryId IN :categoryIds")
    Page<ProductEntity> findByCategoryId(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable); // Added @Param
}
