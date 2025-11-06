package it.back.seller.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.back.seller.entity.SellerDetailEntity;
import it.back.seller.entity.SellerEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class SellerPublicSpecification {

    public static Specification<SellerEntity> search(Long sellerUid, String companyName, String businessRegistrationNumber, String phone, String address) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (sellerUid != null) {
                predicates.add(criteriaBuilder.equal(root.get("sellerUid"), sellerUid));
            }

            if (companyName != null && !companyName.isBlank()) {
                jakarta.persistence.criteria.Expression<String> companyNameWithoutSpaces = criteriaBuilder.function(
                        "replace",
                        String.class,
                        root.get("companyName"),
                        criteriaBuilder.literal(" "),
                        criteriaBuilder.literal("")
                );
                String[] keywords = companyName.trim().split("\s+");
                for (String keyword : keywords) {
                    if (!keyword.isEmpty()) {
                        predicates.add(criteriaBuilder.like(companyNameWithoutSpaces, "%" + keyword + "%"));
                    }
                }
            }

            Join<SellerEntity, SellerDetailEntity> detailJoin = root.join("sellerDetail");

            if (businessRegistrationNumber != null && !businessRegistrationNumber.isBlank()) {
                predicates.add(criteriaBuilder.like(detailJoin.get("businessRegistrationNumber"), "%" + businessRegistrationNumber + "%"));
            }

            if (phone != null && !phone.isBlank()) {
                predicates.add(criteriaBuilder.like(detailJoin.get("phone"), "%" + phone + "%"));
            }

            if (address != null && !address.isBlank()) {
                List<Predicate> addressPredicates = new ArrayList<>();
                String[] keywords = address.trim().split("\s+");

                for (String keyword : keywords) {
                    if (!keyword.isEmpty()) {
                        jakarta.persistence.criteria.Expression<String> addressWithoutSpaces = criteriaBuilder.function(
                                "replace",
                                String.class,
                                detailJoin.get("address"),
                                criteriaBuilder.literal(" "),
                                criteriaBuilder.literal("")
                        );
                        jakarta.persistence.criteria.Expression<String> addressDetailWithoutSpaces = criteriaBuilder.function(
                                "replace",
                                String.class,
                                detailJoin.get("addressDetail"),
                                criteriaBuilder.literal(" "),
                                criteriaBuilder.literal("")
                        );
                        addressPredicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(addressWithoutSpaces, "%" + keyword + "%"),
                                criteriaBuilder.like(addressDetailWithoutSpaces, "%" + keyword + "%")
                        ));
                    }
                }
                if (!addressPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.and(addressPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
