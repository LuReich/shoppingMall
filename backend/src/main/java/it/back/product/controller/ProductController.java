package it.back.product.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.service.ProductService;
import it.back.review.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 모든 상품 리스트 보기, 비로그인 상태에서도 볼 수 있긴한데, 만약 로그인한 상태에서 안된다면 긴급 연락 해주세요
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> getAllProducts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "companyName", required = false) String companyName) {

        PageResponseDTO<ProductDTO> productPageDto = productService.getAllProducts(pageRequestDTO, categoryId, productName, companyName);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(productPageDto));
    }

    // 해당 product_id 보기, 사용 용도 미정
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable("productId") Long productId) {
        ProductDTO productDto = productService.getProductById(productId);
        if (productDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(productDto));
    }

    // 상품 등록 용도인데 미구현
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductEntity>> createProduct(@RequestBody ProductEntity product) {
        ProductEntity savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(savedProduct));
    }

    // 상품 삭제 용인데 미사용 권장
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Product deleted successfully"));
    }

    // 상품 상세 정보 불러오기
    @GetMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductDetail(@PathVariable("productId") Long productId) {
        ProductDetailDTO productDetailDto = productService.getProductDetail(productId);
        if (productDetailDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(productDetailDto));
    }

    // 상품별 리뷰 목록 조회 (페이지네이션 포함)
    @GetMapping("/{productId}/review")
    public ResponseEntity<ApiResponse<PageResponseDTO<ReviewDTO>>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponseDTO<ReviewDTO> reviewPageDto = productService.getProductReviews(productId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(reviewPageDto));
    }

    // 일단 넣어놓기만 한 거라 미사용 권장
    @PostMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<ProductDetailEntity>> createOrUpdateProductDetail(@PathVariable("productId") Long productId,
            @RequestBody ProductDetailEntity detail) {
        detail.setProductId(productId);
        ProductDetailEntity savedProductDetail = productService.saveProductDetail(detail);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(savedProductDetail));
    }
}
