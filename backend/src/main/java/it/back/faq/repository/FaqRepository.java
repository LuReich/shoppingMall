package it.back.faq.repository;

import it.back.faq.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Integer>, JpaSpecificationExecutor<FaqEntity> {
}
