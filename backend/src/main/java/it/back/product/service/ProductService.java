package it.back.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.back.category.service.CategoryService;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductDetailRepository;
import it.back.product.repository.ProductRepository;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.review.service.ReviewService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public PageResponseDTO<ProductDTO> getAllProducts(Pageable pageable, Integer categoryId) {
        Page<ProductEntity> page;
        if (categoryId == null) {
            page = productRepository.findAll(pageable);
        } else {
            List<Integer> categoryIds = categoryService.getCategoryWithChild(categoryId);
            page = productRepository.findByCategoryId(categoryIds, pageable);
        }
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
            return dto;
        }).toList();
        return new PageResponseDTO<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

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
        dto.setReviews(reviewService.getReviewsByProductId(productId));
        return dto;
    }

    public ProductDetailEntity saveProductDetail(ProductDetailEntity detail) {
        return productDetailRepository.save(detail);
    }
}
