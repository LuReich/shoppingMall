package it.back.seller.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import it.back.common.pagination.PageRequestDTO;
import it.back.product.entity.QProductEntity;
import it.back.review.entity.QReviewEntity;
import it.back.seller.dto.SellerPublicListDTO;
import it.back.seller.entity.QSellerDetailEntity;
import it.back.seller.entity.QSellerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SellerPublicListDTO> findSellerPublicList(PageRequestDTO pageRequestDTO, Long sellerUid,
            String companyName, String businessRegistrationNumber, String phone, String address) {

        Pageable pageable = pageRequestDTO.toPageable();
        QSellerEntity seller = QSellerEntity.sellerEntity;
        QSellerDetailEntity sellerDetail = QSellerDetailEntity.sellerDetailEntity;
        QProductEntity product = QProductEntity.productEntity;
        QReviewEntity review = QReviewEntity.reviewEntity;

        var totalLikes = product.likeCount.longValue().sum().coalesce(0L);
        var averageRating = review.rating.avg().coalesce(0.0);
        var totalReviews = review.count().coalesce(0L);

        JPAQuery<Tuple> query = queryFactory
                .select(seller.sellerUid,
                        seller.companyName,
                        seller.sellerEmail,
                        sellerDetail.businessRegistrationNumber,
                        sellerDetail.companyInfo,
                        sellerDetail.phone,
                        sellerDetail.address,
                        sellerDetail.addressDetail,
                        seller.isVerified,
                        seller.isActive,
                        seller.createAt,
                        seller.updateAt,
                        totalLikes,
                        averageRating,
                        totalReviews)
                .from(seller)
                .leftJoin(seller.sellerDetail, sellerDetail)
                .leftJoin(seller.products, product)
                .leftJoin(product.reviews, review)
                .where(
                        seller.isVerified.isTrue(),
                        seller.isActive.isTrue(),
                        eqSellerUid(sellerUid),
                        containsCompanyName(companyName),
                        eqBusinessRegistrationNumber(businessRegistrationNumber),
                        containsPhone(phone),
                        containsAddress(address))
                .groupBy(seller.sellerUid,
                        seller.companyName,
                        seller.sellerEmail,
                        sellerDetail.businessRegistrationNumber,
                        sellerDetail.companyInfo,
                        sellerDetail.phone,
                        sellerDetail.address,
                        sellerDetail.addressDetail,
                        seller.isVerified,
                        seller.isActive,
                        seller.createAt,
                        seller.updateAt);

        // Sorting
        for (Sort.Order o : pageable.getSort()) {
            query.orderBy(createOrderSpecifier(o));
        }

        // Pagination
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Tuple> tuples = query.fetch();

        List<SellerPublicListDTO> content = tuples.stream()
                .map(t -> new SellerPublicListDTO(
                        t.get(seller.sellerUid),
                        t.get(seller.companyName),
                        t.get(seller.sellerEmail),
                        t.get(sellerDetail.businessRegistrationNumber),
                        t.get(sellerDetail.companyInfo),
                        t.get(sellerDetail.phone),
                        t.get(sellerDetail.address),
                        t.get(sellerDetail.addressDetail),
                        t.get(seller.isVerified),
                        t.get(seller.isActive),
                        t.get(seller.createAt),
                        t.get(seller.updateAt),
                        t.get(totalLikes),
                        t.get(averageRating),
                        t.get(totalReviews)))
                .collect(java.util.stream.Collectors.toList());


        // Count query
        JPAQuery<Long> countQuery = queryFactory
                .select(seller.countDistinct())
                .from(seller)
                .leftJoin(seller.sellerDetail, sellerDetail)
                .where(
                        seller.isVerified.isTrue(),
                        seller.isActive.isTrue(),
                        eqSellerUid(sellerUid),
                        containsCompanyName(companyName),
                        eqBusinessRegistrationNumber(businessRegistrationNumber),
                        containsPhone(phone),
                        containsAddress(address));

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> createOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;
        String property = order.getProperty();

        switch (property) {
            case "totalLikes":
                return new OrderSpecifier<>(direction, QProductEntity.productEntity.likeCount.longValue().sum().coalesce(0L));
            case "averageRating":
                return new OrderSpecifier<>(direction, QReviewEntity.reviewEntity.rating.avg().coalesce(0.0));
            case "totalReviews":
                return new OrderSpecifier<>(direction, QReviewEntity.reviewEntity.count().coalesce(0L));
            case "companyName":
                return new OrderSpecifier<>(direction, QSellerEntity.sellerEntity.companyName);
            case "createAt":
                return new OrderSpecifier<>(direction, QSellerEntity.sellerEntity.createAt);
            default:
                // Default sorting
                return new OrderSpecifier<>(Order.DESC, QSellerEntity.sellerEntity.createAt);
        }
    }

    private BooleanExpression eqSellerUid(Long sellerUid) {
        return sellerUid != null ? QSellerEntity.sellerEntity.sellerUid.eq(sellerUid) : null;
    }

    private BooleanExpression containsCompanyName(String companyName) {
        return StringUtils.hasText(companyName) ? QSellerEntity.sellerEntity.companyName.contains(companyName) : null;
    }

    private BooleanExpression eqBusinessRegistrationNumber(String businessRegistrationNumber) {
        return StringUtils.hasText(businessRegistrationNumber)
                ? QSellerDetailEntity.sellerDetailEntity.businessRegistrationNumber.eq(businessRegistrationNumber)
                : null;
    }

    private BooleanExpression containsPhone(String phone) {
        return StringUtils.hasText(phone) ? QSellerDetailEntity.sellerDetailEntity.phone.contains(phone) : null;
    }

    private BooleanExpression containsAddress(String address) {
        return StringUtils.hasText(address) ? QSellerDetailEntity.sellerDetailEntity.address.contains(address) : null;
    }
}
