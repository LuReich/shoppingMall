package it.back.seller.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import it.back.common.pagination.PageRequestDTO;
import it.back.product.entity.QProductEntity;
import it.back.review.entity.QReviewEntity;
import it.back.seller.dto.SellerPublicDTO;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SellerPublicDTO> findSellerPublicInfoById(Long sellerUid) {
        QSellerEntity seller = QSellerEntity.sellerEntity;
        QSellerDetailEntity sellerDetail = QSellerDetailEntity.sellerDetailEntity;
        QProductEntity product = QProductEntity.productEntity;
        QReviewEntity review = QReviewEntity.reviewEntity;

        var totalLikes = product.likeCount.longValue().sum().coalesce(0L);
        var averageRating = review.rating.avg().coalesce(0.0);
        var totalReviews = review.count().coalesce(0L);

        Tuple tuple = queryFactory
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
                        seller.sellerUid.eq(sellerUid),
                        seller.isActive.isTrue())
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
                        seller.updateAt)
                .fetchOne();

        if (tuple == null) {
            return Optional.empty();
        }

        SellerPublicDTO dto = new SellerPublicDTO();
        dto.setSellerUid(tuple.get(seller.sellerUid));
        dto.setCompanyName(tuple.get(seller.companyName));
        dto.setSellerEmail(tuple.get(seller.sellerEmail));
        dto.setBusinessRegistrationNumber(tuple.get(sellerDetail.businessRegistrationNumber));
        dto.setCompanyInfo(tuple.get(sellerDetail.companyInfo));
        dto.setPhone(tuple.get(sellerDetail.phone));
        dto.setAddress(tuple.get(sellerDetail.address));
        dto.setAddressDetail(tuple.get(sellerDetail.addressDetail));
        dto.setVerified(tuple.get(seller.isVerified));
        dto.setActive(tuple.get(seller.isActive));
        dto.setCreateAt(tuple.get(seller.createAt));
        dto.setUpdateAt(tuple.get(seller.updateAt));
        dto.setTotalLikes(tuple.get(totalLikes));
        dto.setAverageRating(tuple.get(averageRating));
        dto.setTotalReviews(tuple.get(totalReviews));

        return Optional.of(dto);
    }

    @Override
    public Page<SellerPublicListDTO> findSellerPublicList(PageRequestDTO pageRequestDTO, Long sellerUid,
            String companyName, String businessRegistrationNumber, String phone, String address, Boolean isVerified) {

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
                        seller.isActive.isTrue(),
                        eqSellerUid(sellerUid),
                        containsCompanyName(companyName),
                        containsBusinessRegistrationNumber(businessRegistrationNumber),
                        containsPhone(phone),
                        containsAddress(address),
                        eqIsVerified(isVerified))
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
                        seller.isActive.isTrue(),
                        eqSellerUid(sellerUid),
                        containsCompanyName(companyName),
                        containsBusinessRegistrationNumber(businessRegistrationNumber),
                        containsPhone(phone),
                        containsAddress(address),
                        eqIsVerified(isVerified));

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> createOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;
        String property = order.getProperty();

        switch (property) {
            case "sellerUid":
                return new OrderSpecifier<>(direction, QSellerEntity.sellerEntity.sellerUid);
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
        if (!StringUtils.hasText(companyName)) {
            return null;
        }
        String processedSearchTerm = companyName.replace(" ", "").toLowerCase();
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", QSellerEntity.sellerEntity.companyName)
                .lower()
                .contains(processedSearchTerm);
    }

    private BooleanExpression containsBusinessRegistrationNumber(String businessRegistrationNumber) {
        return StringUtils.hasText(businessRegistrationNumber)
                ? QSellerDetailEntity.sellerDetailEntity.businessRegistrationNumber.contains(businessRegistrationNumber)
                : null;
    }

    private BooleanExpression containsPhone(String phone) {
        return StringUtils.hasText(phone) ? QSellerDetailEntity.sellerDetailEntity.phone.contains(phone) : null;
    }

    private BooleanExpression containsAddress(String address) {
        return StringUtils.hasText(address) ? QSellerDetailEntity.sellerDetailEntity.address.contains(address) : null;
    }

    private BooleanExpression eqIsVerified(Boolean isVerified) {
        return isVerified != null ? QSellerEntity.sellerEntity.isVerified.eq(isVerified) : null;
    }
}
