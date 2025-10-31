package it.back.product.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductUpdateDTO;
import it.back.product.dto.ProductCreateDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.dto.ProductListDTO;
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
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductListDTO>>> getAllProducts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "companyName", required = false) String companyName) {

        PageResponseDTO<ProductListDTO> productPageDto = productService.getAllProducts(pageRequestDTO, categoryId, productName, companyName);
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

    // 상품 등록 (이미지 포함)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            Authentication authentication,
            @RequestPart("productData") ProductCreateDTO productCreateDTO,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart(name = "subImages", required = false) List<MultipartFile> subImages) {

        Long sellerUid = getSellerUidFromAuthWithRoleCheck(authentication);

        ProductDTO savedProduct = productService.createProduct(
                sellerUid,
                productCreateDTO,
                mainImage,
                subImages);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(savedProduct));
    }

    // 상품 수정 (이미지 수정/삭제 포함)
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long productId,
            Authentication authentication,
            @RequestPart("productData") ProductUpdateDTO productUpdateDTO,
            @RequestPart(name = "newMainImage", required = false) MultipartFile newMainImage,
            @RequestPart(name = "newSubImages", required = false) List<MultipartFile> newSubImages) {

        Long sellerUid = getSellerUidFromAuthWithRoleCheck(authentication);

        ProductDTO updatedProduct = productService.updateProduct(
                sellerUid,
                productId,
                productUpdateDTO,
                newMainImage,
                newSubImages);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(updatedProduct));
    }

    // 상품 삭제 용인데 미사용 권장
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Product deleted successfully"));
    }

    // 상품 삭제 (Soft Delete)
    @PatchMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse<String>> softDeleteProduct(
            @PathVariable Long productId,
            Authentication authentication) {

        Long sellerUid = getSellerUidFromAuthWithRoleCheck(authentication);
        productService.softDeleteProduct(sellerUid, productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("상품이 성공적으로 삭제(비활성화)되었습니다."));
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

    // React Quill 에디터에서 상품 상세 설명 이미지 업로드
    @PostMapping("/{productId}/description/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadDescriptionImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile imageFile,
            Authentication authentication) {

        Long sellerUid = getSellerUidFromAuthWithRoleCheck(authentication);

        String imageUrl = productService.uploadDescriptionImage(sellerUid, productId, imageFile);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    // React Quill 에디터에서 상품 상세 설명 이미지 임시 업로드 (productId 없음)
    @PostMapping("/description/temp")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadTempDescriptionImage(
            @RequestParam("image") MultipartFile imageFile,
            Authentication authentication) {

        getSellerUidFromAuthWithRoleCheck(authentication); // 판매자 권한만 있는지 체크

        String tempImageUrl = productService.uploadTempDescriptionImage(imageFile);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", tempImageUrl);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    // 일단 넣어놓기만 한 거라 미사용 권장
    @PostMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<ProductDetailEntity>> createOrUpdateProductDetail(@PathVariable("productId") Long productId,
            @RequestBody ProductDetailEntity detail) {
        detail.setProductId(productId);
        ProductDetailEntity savedProductDetail = productService.saveProductDetail(detail);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(savedProductDetail));
    }

    // JWT에서 sellerUid 추출 및 SELLER 권한 체크
    private Long getSellerUidFromAuthWithRoleCheck(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> details)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }
        Object roleObj = details.get("role");
        if (roleObj == null || !"SELLER".equals(roleObj.toString())) {
            throw new IllegalStateException("판매자(SELLER) 권한이 필요합니다.");
        }
        Object uidObj = details.get("uid");
        if (uidObj instanceof Integer i) {
            return i.longValue();
        } else if (uidObj instanceof Long l) {
            return l;
        } else {
            throw new IllegalStateException("uid 타입이 올바르지 않습니다.");
        }
    }
}
