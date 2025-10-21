package it.back.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import it.back.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByProduct_ProductId(Long productId);
}