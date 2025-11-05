package it.back.faq.specification;

import it.back.faq.entity.FaqEntity;
import org.springframework.data.jpa.domain.Specification;

public class FaqSpecification {

    public static Specification<FaqEntity> equalFaqTarget(FaqEntity.FaqTarget faqTarget) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("faqTarget"), faqTarget);
    }

    public static Specification<FaqEntity> equalFaqCategory(FaqEntity.FaqCategory faqCategory) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("faqCategory"), faqCategory);
    }
}
