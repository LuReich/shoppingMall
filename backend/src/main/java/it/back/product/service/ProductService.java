package it.back.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.category.service.CategoryService;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductDetailRepository;
import it.back.product.repository.ProductRepository;
import it.back.product.specification.ProductSpecifications;
import it.back.review.dto.ReviewDTO;
import it.back.review.entity.ReviewEntity;
import it.back.review.service.ReviewService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    // 상품별 리뷰 목록 조회 (페이지네이션)
    public PageResponseDTO<ReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        Page<ReviewEntity> page = reviewService.getReviewsByProductIdPaged(productId, pageable);
        List<ReviewDTO> dtos = page.getContent().stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(review.getReviewId());
            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setCreateAt(review.getCreateAt());
            dto.setUpdateAt(review.getUpdateAt());
            dto.setBuyerNickname(review.getBuyer().getNickname());
            dto.setBuyerUid(review.getBuyer().getBuyerUid());
            dto.setSellerCompanyName(review.getProduct().getSeller().getCompanyName());
            dto.setSellerUid(review.getProduct().getSeller().getSellerUid());
            dto.setProductName(review.getProduct().getProductName());
            dto.setProductId(review.getProduct().getProductId());
            dto.setOrderDetailId(review.getOrderDetail().getOrderDetailId());
            return dto;
        }).toList();
        return new PageResponseDTO<>(page, dtos);
    }

    public PageResponseDTO<ProductDTO> getAllProducts(PageRequestDTO pageRequestDTO, Integer categoryId, String productName, String companyName) {

        Pageable pageable = pageRequestDTO.toPageable();

        Specification<ProductEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Add fetch join for Seller to avoid N+1 queries
        spec = spec.and(ProductSpecifications.withSeller());

        spec = spec.and(ProductSpecifications.nameContains(productName));
        spec = spec.and(ProductSpecifications.companyNameContains(companyName));

        if (categoryId != null) {
            List<Integer> categoryIds = categoryService.getCategoryWithChild(categoryId);
            spec = spec.and(ProductSpecifications.inCategory(categoryIds));
        }

        Page<ProductEntity> page = productRepository.findAll(spec, pageable);

        List<ProductDTO> dtos = page.getContent().stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setSellerUid(product.getSeller() != null ? product.getSeller().getSellerUid() : null);
            dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            dto.setThumbnailUrl(product.getThumbnailUrl());
            dto.setCreateAt(product.getCreateAt());
            dto.setUpdateAt(product.getUpdateAt());
            dto.setIsDeleted(product.getIsDeleted());
            dto.setCompanyName(product.getSeller() != null ? product.getSeller().getCompanyName() : null);
            return dto;
        }).toList();

        return new PageResponseDTO<>(page, dtos);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Optional<ProductEntity> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return null;
        }
        ProductEntity product = productOpt.get();
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setSellerUid(product.getSeller() != null ? product.getSeller().getSellerUid() : null);
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
        dto.setProductName(product.getProductName());
        dto.setCompanyName(product.getSeller() != null ? product.getSeller().getCompanyName() : null);
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setCreateAt(product.getCreateAt());
        dto.setUpdateAt(product.getUpdateAt());
        dto.setIsDeleted(product.getIsDeleted());
        return dto;
    }

    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setIsDeleted(true);
            productRepository.save(product);
        });
    }

    public ProductDetailDTO getProductDetail(Long productId) {
        Optional<ProductDetailEntity> detailOpt = productDetailRepository.findById(productId);
        if (detailOpt.isEmpty()) {
            return null;
        }
        ProductDetailEntity detail = detailOpt.get();
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(detail.getProductId());
        dto.setDescription(detail.getDescription());
        dto.setShippingInfo(detail.getShippingInfo());
        // 리뷰는 별도 API에서 제공
        return dto;
    }

    // 상품별 리뷰 목록 조회 (ProductController에서 사용)
    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    public ProductDetailEntity saveProductDetail(ProductDetailEntity detail) {
        return productDetailRepository.save(detail);
    }
}
