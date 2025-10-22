package it.back.product.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.service.ProductService;
import it.back.review.dto.ReviewDTO;
import it.back.review.service.ReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProductDTO>> getAllProducts(
            Pageable pageable, @RequestParam(name = "categoryId", required = false) Integer categoryId) {

        Page<ProductEntity> page = productService.getAllProducts(pageable, categoryId);
        List<ProductDTO> dtos = page.getContent().stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setSellerUid(product.getSeller() != null ? product.getSeller().getSellerUid() : null);
            dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            dto.setThumbnailUrl(product.getThumbnailUrl());
            dto.setCreateAt(product.getCreateAt());
            dto.setUpdateAt(product.getUpdateAt());
            dto.setIsDeleted(product.getIsDeleted());
            return dto;
        }).collect(Collectors.toList());
        PageResponseDTO<ProductDTO> response = new PageResponseDTO<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductEntity> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductEntity product = productOpt.get();
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setSellerUid(product.getSeller() != null ? product.getSeller().getSellerUid() : null);
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setCreateAt(product.getCreateAt());
        dto.setUpdateAt(product.getUpdateAt());
        dto.setIsDeleted(product.getIsDeleted());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long id) {
        Optional<ProductDetailEntity> detailOpt = productService.getProductDetail(id);
        if (detailOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductDetailEntity detail = detailOpt.get();
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(detail.getProductId());
        dto.setDescription(detail.getDescription());
        dto.setShippingInfo(detail.getShippingInfo());
        // 리뷰 목록 추가 (ReviewDTO로 변환)
        dto.setReviews(reviewService.getReviewsByProductId(id)
                .stream()
                .map(review -> {
                    ReviewDTO r = new ReviewDTO();
                    r.setReviewId(review.getReviewId());
                    r.setContent(review.getContent());
                    r.setRating(review.getRating());
                    r.setCreatedAt(review.getCreatedAt());
                    r.setWriter(review.getWriter());
                    return r;
                })
                .collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/detail")
    public ResponseEntity<ProductDetailEntity> createOrUpdateProductDetail(@PathVariable Long id,
            @RequestBody ProductDetailEntity detail) {
        detail.setProductId(id);
        return ResponseEntity.ok(productService.saveProductDetail(detail));
    }
}
