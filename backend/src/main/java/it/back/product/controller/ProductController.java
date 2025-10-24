package it.back.product.controller;

// ...existing code...
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
// ...existing code...
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 모든 상품 리스트 보기, 비로그인 상태에서도 볼 수 있긴한데, 만약 로그인한 상태에서 안된다면 긴급 연락 해주세요
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProductDTO>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "categoryId", required = false) Integer categoryId) {
        return ResponseEntity.ok(productService.getAllProducts(pageable, categoryId));
    }

    // 해당 product_id 보기, 사용 용도 미정
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO dto = productService.getProductById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // 상품 등록 용도인데 미구현
    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    // 상품 삭제 용인데 미사용 권장
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 상품 상세 정보 불러오기
    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable("id") Long id) {
        ProductDetailDTO dto = productService.getProductDetail(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // 일단 넣어놓기만 한 거라 미사용 권장
    @PostMapping("/{id}/detail")
    public ResponseEntity<ProductDetailEntity> createOrUpdateProductDetail(@PathVariable Long id,
            @RequestBody ProductDetailEntity detail) {
        detail.setProductId(id);
        return ResponseEntity.ok(productService.saveProductDetail(detail));
    }
}
