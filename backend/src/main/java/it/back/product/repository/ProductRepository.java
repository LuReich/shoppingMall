
package it.back.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Added this

import it.back.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    @Query("SELECT p FROM ProductEntity p WHERE REPLACE(p.productName, ' ', '') LIKE %:name%")
    Page<ProductEntity> findByProductNameIgnoreSpace(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.categoryId IN :categoryIds AND REPLACE(p.productName, ' ', '') LIKE %:name%")
    Page<ProductEntity> findByCategoryIdAndProductNameIgnoreSpace(@Param("categoryIds") List<Integer> categoryIds, @Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.categoryId IN :categoryIds")
    Page<ProductEntity> findByCategoryId(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable); // Added @Param
}
