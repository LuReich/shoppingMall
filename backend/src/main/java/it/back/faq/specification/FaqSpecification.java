package it.back.faq.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import it.back.faq.entity.FaqEntity;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

@Component
public class FaqSpecification {

    public static Specification<FaqEntity> equalFaqTarget(FaqEntity.FaqTarget faqTarget) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("faqTarget"), faqTarget);
    }

    public static Specification<FaqEntity> equalFaqCategory(FaqEntity.FaqCategory faqCategory) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("faqCategory"), faqCategory);
    }

    public static Specification<FaqEntity> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            // DB 필드에서 공백을 제거하는 표현식
            jakarta.persistence.criteria.Expression<String> questionWithoutSpaces = criteriaBuilder.function(
                    "replace", String.class, root.get("faqQuestion"), criteriaBuilder.literal(" "), criteriaBuilder.literal(""));
            jakarta.persistence.criteria.Expression<String> answerWithoutSpaces = criteriaBuilder.function(
                    "replace", String.class, root.get("faqAnswer"), criteriaBuilder.literal(" "), criteriaBuilder.literal(""));

            // 검색어를 공백 기준으로 분리
            String[] keywords = keyword.trim().split("\\s+");

            List<Predicate> keywordPredicates = new java.util.ArrayList<>();
            for (String kw : keywords) {
                if (!kw.isEmpty()) {
                    // 각 키워드에 대해 질문 또는 답변에 포함되는지 확인
                    Predicate questionLike = criteriaBuilder.like(questionWithoutSpaces, "%" + kw + "%");
                    Predicate answerLike = criteriaBuilder.like(answerWithoutSpaces, "%" + kw + "%");
                    keywordPredicates.add(criteriaBuilder.or(questionLike, answerLike));
                }
            }

            if (keywordPredicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // 모든 키워드 조건을 AND로 결합
            return criteriaBuilder.and(keywordPredicates.toArray(new Predicate[0]));
        };
    }
}
