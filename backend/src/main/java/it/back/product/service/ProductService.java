package it.back.product.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.back.category.service.CategoryService;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.FileUtils;
import it.back.product.dto.ProductCreateDTO;
import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.dto.ProductListDTO;
import it.back.product.dto.ProductUpdateDTO;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.entity.ProductImageEntity;
import it.back.product.repository.ProductDetailRepository;
import it.back.product.repository.ProductImageRepository;
import it.back.product.repository.ProductRepository;
import it.back.product.specification.ProductSpecifications;
import it.back.review.dto.ReviewDTO;
import it.back.review.entity.ReviewEntity;
import it.back.review.service.ReviewService;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository; // 판매자 정보 조회를 위해 추가
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageRepository productImageRepository; // 상품 이미지 저장을 위해 추가
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final FileUtils fileUtils; // 파일 처리를 위해 추가

    @Value("${file.upload-dir}")
    private String uploadDir; // application.yml에서 파일 업로드 경로 주입

    private String getOsIndependentPath(String... paths) {
        return String.join(File.separator, paths); // OS 독립적인 경로 생성을 위한 헬퍼 메서드
    }

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
            dto.setCompanyName(review.getProduct().getSeller().getCompanyName());
            dto.setSellerUid(review.getProduct().getSeller().getSellerUid());
            dto.setProductName(review.getProduct().getProductName());
            dto.setProductId(review.getProduct().getProductId());
            dto.setOrderDetailId(review.getOrderDetail().getOrderDetailId());
            return dto;
        }).toList();
        return new PageResponseDTO<>(page, dtos);
    }

    public PageResponseDTO<ProductListDTO> getAllProducts(PageRequestDTO pageRequestDTO, Integer categoryId, String productName, String companyName) {

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

        List<ProductListDTO> dtos = page.getContent().stream().map(product -> {
            ProductListDTO dto = new ProductListDTO();
            dto.setProductId(product.getProductId());
            dto.setSellerUid(product.getSeller() != null ? product.getSeller().getSellerUid() : null);
            dto.setCategoryId(product.getCategoryId()); // 직접 매핑된 categoryId 사용
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
        Optional<ProductEntity> productOpt = productRepository.findByIdWithImages(id);
        if (productOpt.isEmpty()) {
            return null;
        }
        ProductEntity product = productOpt.get();
        return new ProductDTO(product);
    }

    @Transactional // 트랜잭션 적용: 상품 정보와 이미지 저장이 하나의 단위로 처리되도록
    public ProductDTO createProduct(Long sellerUid, ProductCreateDTO dto, MultipartFile mainImage,
            List<MultipartFile> subImages, List<MultipartFile> descriptionImages) {
        // 1. 판매자 정보 조회
        SellerEntity seller = sellerRepository.findById(sellerUid)
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보를 찾을 수 없습니다."));

        // 2. 상품 기본 정보 저장 (product_id 생성을 위해)
        ProductEntity product = ProductEntity.builder()
                .seller(seller)
                .categoryId(dto.getCategoryId())
                .productName(dto.getProductName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .isDeleted(false) // isDeleted 기본값 설정
                .build();
        ProductEntity savedProduct = productRepository.save(product);
        Long productId = savedProduct.getProductId();

        // 2-1. description 이미지들을 먼저 저장하고 실제 URL 생성
        String descriptionPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId), "description");
        List<String> descriptionImageUrls = new ArrayList<>();
        if (descriptionImages != null && !descriptionImages.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(descriptionPath));
                for (MultipartFile imageFile : descriptionImages) {
                    if (imageFile.isEmpty()) {
                        continue;
                    }
                    String storedName = fileUtils.saveFile(imageFile, descriptionPath);
                    String imageUrl = "/product/" + productId + "/description/" + storedName;
                    descriptionImageUrls.add(imageUrl);
                }
            } catch (IOException e) {
                throw new java.io.UncheckedIOException("상품 설명 이미지 저장 중 오류가 발생했습니다.", e);
            }
        }

        // 2-2. 상품 상세 정보 저장 (product_detail)
        ProductDetailEntity detail = new ProductDetailEntity();
        detail.setProduct(savedProduct); // 연관관계 설정
        // description HTML의 이미지를 실제 저장된 URL로 교체
        detail.setDescription(replaceImagePlaceholdersWithUrls(dto.getDescription(), descriptionImageUrls));
        detail.setShippingInfo(dto.getShippingInfo());
        productDetailRepository.save(detail);

        // ProductEntity에 detail을 설정하여 양방향 관계를 메모리상에서 유지
        savedProduct.setProductDetail(detail);

        // 3. 파일 저장 경로 설정 및 생성
        // 예: C:/ourshop/product/1
        String productPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId));
        String mainImagePath = getOsIndependentPath(productPath, "main");
        String subPath = getOsIndependentPath(productPath, "sub");

        try {
            Files.createDirectories(Paths.get(mainImagePath));
            Files.createDirectories(Paths.get(subPath));
        } catch (IOException e) {
            // UncheckedIOException을 사용하여 트랜잭션 롤백이 가능하도록 함
            throw new java.io.UncheckedIOException("상품 이미지 폴더 생성에 실패했습니다.", e);
        }

        // 4. 대표 이미지 저장 및 상품 정보 업데이트
        String mainImageStoredName = fileUtils.saveFile(mainImage, mainImagePath);
        // DB에는 웹 접근이 가능한 URL 경로를 저장
        String mainImageUrl = "/product/" + productId + "/main/" + mainImageStoredName;
        savedProduct.setThumbnailUrl(mainImageUrl);

        // 5. 서브(추가) 이미지 저장
        if (subImages != null && !subImages.isEmpty()) {
            List<ProductImageEntity> imageEntities = new ArrayList<>();
            int sortOrder = 0;
            for (MultipartFile file : subImages) {
                if (file.isEmpty()) {
                    continue;
                }
                String storedName = fileUtils.saveFile(file, subPath); // FileUtils의 saveFile 사용
                String subImageUrl = "/product/" + productId + "/sub/" + storedName;

                ProductImageEntity imageEntity = ProductImageEntity.builder()
                        .product(savedProduct).imageName(file.getOriginalFilename()).storedName(storedName)
                        .imagePath(subImageUrl).imageSize(file.getSize()).sortOrder(sortOrder++).build();
                imageEntities.add(imageEntity);
            }
            List<ProductImageEntity> savedImages = productImageRepository.saveAll(imageEntities); // saveAll 사용
            // 영속성 컨텍스트 내의 ProductEntity에 이미지 리스트를 명시적으로 설정
            savedProduct.setProductImages(savedImages);
        }

        // 6. 모든 정보가 포함된 완전한 Entity를 다시 조회하여 DTO로 변환 후 반환
        ProductEntity finalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("저장된 상품을 다시 찾을 수 없습니다."));
        return new ProductDTO(finalProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long sellerUid, Long productId, ProductUpdateDTO dto,
            MultipartFile newMainImage, List<MultipartFile> newSubImages) {
        // 1. 상품 조회 및 권한 확인
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        if (!product.getSeller().getSellerUid().equals(sellerUid)) {
            throw new AccessDeniedException("해당 상품에 대한 수정 권한이 없습니다.");
        }

        // 2. 상품 기본 정보 업데이트
        if (dto.getProductName() != null) {
            product.setProductName(dto.getProductName());
        }
        if (dto.getCategoryId() != null) {
            product.setCategoryId(dto.getCategoryId());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }

        // 3. 상품 상세 정보 업데이트
        ProductDetailEntity detail = productDetailRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("상품 상세 정보를 찾을 수 없습니다. ID: " + productId));
        // 상품 설명(description)의 임시 이미지 처리
        if (dto.getDescription() != null) {
            try {
                // 1. 기존 설명에서 이미지 URL 목록 추출
                List<String> oldImageUrls = extractImageUrlsFromHtml(detail.getDescription());
                // 2. 새 설명으로 업데이트하고, 새 설명의 이미지 URL 목록 추출
                String newDescription = moveTempImagesToPermanent(productId, dto.getDescription());
                detail.setDescription(newDescription);
                List<String> newImageUrls = extractImageUrlsFromHtml(newDescription);
                // 3. 삭제된 이미지(고아 이미지) 파일 시스템에서 삭제
                deleteOrphanedDescriptionImages(oldImageUrls, newImageUrls);
            } catch (IOException e) {
                throw new java.io.UncheckedIOException("상품 설명 이미지 처리 중 오류가 발생했습니다.", e);
            }
        }
        if (dto.getShippingInfo() != null) {
            detail.setShippingInfo(dto.getShippingInfo());
        }
        productDetailRepository.save(detail); // 상세 정보 저장

        // 4. 메인 이미지 업데이트
        if (newMainImage != null && !newMainImage.isEmpty()) {
            // 기존 메인 이미지 파일 삭제
            if (product.getThumbnailUrl() != null && !product.getThumbnailUrl().isEmpty()) {
                String oldMainImageFullPath = getOsIndependentPath(uploadDir, product.getThumbnailUrl());
                fileUtils.deleteFile(oldMainImageFullPath);
            }
            // 새 메인 이미지 저장
            String productBaseDir = getOsIndependentPath(uploadDir, "product", String.valueOf(productId));
            String mainImageDir = getOsIndependentPath(productBaseDir, "main");
            String storedName = fileUtils.saveFile(newMainImage, mainImageDir);
            String newMainImageUrlPath = "/product/" + productId + "/main/" + storedName;
            product.setThumbnailUrl(newMainImageUrlPath);
        }

        // 5. 서브 이미지 삭제
        if (dto.getDeleteImageIds() != null && !dto.getDeleteImageIds().isEmpty()) {
            List<ProductImageEntity> imagesToDelete = productImageRepository.findAllById(dto.getDeleteImageIds());
            for (ProductImageEntity image : imagesToDelete) {
                if (!image.getProduct().getProductId().equals(productId)) {
                    // 다른 상품의 이미지를 삭제하려는 시도 방지
                    throw new AccessDeniedException("삭제하려는 이미지가 해당 상품에 속하지 않습니다.");
                }
                // imagePath가 /product/ 또는 /temp/ 로 시작하므로 앞부분을 제거하고 실제 파일 시스템 경로를 조합
                String relativePath = image.getImagePath().startsWith("/") ? image.getImagePath().substring(1) : image.getImagePath();
                String fullPath = getOsIndependentPath(uploadDir, relativePath);
                fileUtils.deleteFile(fullPath);
                // ProductEntity의 productImages 리스트에서도 제거하여 orphanRemoval이 동작하도록 함
                product.getProductImages().remove(image);
            }
            productImageRepository.deleteAll(imagesToDelete); // DB에서 이미지 엔티티 삭제
        }

        // 6. 새 서브 이미지 추가
        if (newSubImages != null && !newSubImages.isEmpty()) {
            List<ProductImageEntity> newImageEntities = new ArrayList<>();
            // 현재 존재하는 이미지들의 sortOrder를 고려하여 새로운 이미지의 sortOrder를 설정
            int maxSortOrder = product.getProductImages().stream()
                    .mapToInt(ProductImageEntity::getSortOrder)
                    .max().orElse(-1);
            int currentSortOrder = maxSortOrder + 1;

            String productBaseDir = getOsIndependentPath(uploadDir, "product", String.valueOf(productId));
            String subImageDir = getOsIndependentPath(productBaseDir, "sub");

            for (MultipartFile file : newSubImages) {
                if (file.isEmpty()) {
                    continue;
                }
                String storedName = fileUtils.saveFile(file, subImageDir);
                String newSubImageUrl = "/product/" + productId + "/sub/" + storedName;
                ProductImageEntity imageEntity = ProductImageEntity.builder()
                        .product(product).imageName(file.getOriginalFilename()).storedName(storedName)
                        .imagePath(newSubImageUrl).imageSize(file.getSize()).sortOrder(currentSortOrder++).build();
                newImageEntities.add(imageEntity);
            }
            productImageRepository.saveAll(newImageEntities); // DB에 새 이미지 엔티티 저장
            product.getProductImages().addAll(newImageEntities); // ProductEntity 리스트에 추가
        }

        productRepository.save(product); // 최종 상품 정보 저장 (cascade로 productDetail도 저장됨)
        // 모든 정보가 포함된 완전한 Entity를 다시 조회하여 DTO로 변환 후 반환
        return new ProductDTO(productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("업데이트된 상품을 다시 찾을 수 없습니다. ID: " + productId)));
    }

    /**
     * description HTML에서 blob: URL이나 임시 이미지 경로를 실제 저장된 이미지 URL로 교체합니다. img 태그를
     * 순서대로 찾아서 descriptionImageUrls의 순서대로 교체합니다.
     *
     * @param htmlContent 원본 HTML (blob: URL 또는 임시 경로 포함)
     * @param descriptionImageUrls 실제 저장된 이미지 URL 리스트
     * @return URL이 교체된 HTML 문자열
     */
    private String replaceImagePlaceholdersWithUrls(String htmlContent, List<String> descriptionImageUrls) {
        if (htmlContent == null || htmlContent.isEmpty() || descriptionImageUrls.isEmpty()) {
            return htmlContent;
        }

        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img");

        int urlIndex = 0;
        for (Element img : images) {
            if (urlIndex >= descriptionImageUrls.size()) {
                break; // URL이 부족하면 중단
            }
            // blob: URL이나 임시 경로를 실제 저장된 URL로 교체
            String src = img.attr("src");
            if (src.startsWith("blob:") || src.contains("temp")) {
                img.attr("src", descriptionImageUrls.get(urlIndex));
                urlIndex++;
            }
        }

        return doc.body().html();
    }

    /**
     * 상품 설명(HTML)에 포함된 임시 이미지를 영구 경로로 이동시키고, 이미지 경로를 실제 URL로 교체하여 반환합니다.
     *
     * @param productId 상품 ID
     * @param htmlContent 임시 이미지가 포함된 HTML 문자열
     * @return 이미지 경로가 교체된 HTML 문자열
     * @throws IOException 파일 이동 중 오류 발생 시
     */
    private String moveTempImagesToPermanent(Long productId, String htmlContent) throws IOException {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return htmlContent;
        }

        Document doc = Jsoup.parse(htmlContent);
        // /temp/ 또는 localhost:9090/temp/ 로 시작하는 src를 가진 img 태그 선택
        Elements images = doc.select("img[src*=/temp/]");

        if (images.isEmpty()) {
            return htmlContent; // 처리할 임시 이미지가 없으면 원본 HTML 반환
        }

        String permanentPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId), "description");
        Files.createDirectories(Paths.get(permanentPath));

        for (Element img : images) {
            String tempSrc = img.attr("src"); // 예: /temp/uuid.png 또는 http://localhost:9090/temp/uuid.png

            // URL에서 파일명만 추출
            String tempFileName;
            if (tempSrc.contains("/temp/")) {
                tempFileName = tempSrc.substring(tempSrc.indexOf("/temp/") + 6); // "/temp/" 이후의 파일명
            } else {
                continue; // temp 경로가 아니면 스킵
            }

            Path sourcePath = Paths.get(getOsIndependentPath(uploadDir, "temp"), tempFileName);
            Path destinationPath = Paths.get(permanentPath, tempFileName);

            // 파일이 임시 폴더에 실제로 존재하는지 확인 후 이동
            if (Files.exists(sourcePath)) {
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // 새로운 영구 URL로 src 속성 변경 (상대 경로로 저장, 프론트엔드에서 base URL 추가)
                String permanentUrl = "/product/" + productId + "/description/" + tempFileName;
                img.attr("src", permanentUrl);
            } else {
                // 임시 파일이 없는 경우(오류 등), 해당 img 태그를 제거하거나 대체 이미지를 넣을 수 있음
                // 여기서는 일단 그대로 둠 (broken image로 표시됨)
            }
        }

        return doc.body().html();
    }

    /**
     * HTML 문자열에서 모든 이미지의 src 속성 값을 추출합니다.
     *
     * @param htmlContent HTML 문자열
     * @return 이미지 URL 목록
     */
    private List<String> extractImageUrlsFromHtml(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return new ArrayList<>();
        }
        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img");
        return images.stream()
                .map(img -> img.attr("src"))
                .filter(src -> src != null && !src.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * 이전 이미지 URL 목록과 새 이미지 URL 목록을 비교하여, 더 이상 사용되지 않는 '고아 이미지'를 파일 시스템에서 삭제합니다.
     *
     * @param oldImageUrls 이전 HTML의 이미지 URL 목록
     * @param newImageUrls 새 HTML의 이미지 URL 목록
     */
    private void deleteOrphanedDescriptionImages(List<String> oldImageUrls, List<String> newImageUrls) {
        Set<String> newUrlSet = new java.util.HashSet<>(newImageUrls);

        oldImageUrls.stream()
                .filter(oldUrl -> !newUrlSet.contains(oldUrl)) // 새 목록에 없는 이전 URL(삭제된 이미지) 필터링
                .filter(urlToDelete -> urlToDelete.contains("/product/")) // 시스템 경로가 아닌 웹 URL만 대상으로 함
                .forEach(urlToDelete -> {
                    try {
                        String relativePath = urlToDelete.startsWith("/") ? urlToDelete.substring(1) : urlToDelete;
                        String fullPath = getOsIndependentPath(uploadDir, relativePath);
                        fileUtils.deleteFile(fullPath);
                    } catch (Exception e) {
                        // 파일 삭제 실패 시 로그를 남길 수 있지만, 일단 진행은 막지 않음
                    }
                });
    }

    /**
     * Quill 에디터의 이미지를 임시 폴더에 업로드하고 임시 URL을 반환합니다.
     *
     * @param imageFile 업로드된 이미지 파일
     * @return 웹에서 접근 가능한 임시 이미지 URL
     */
    @Transactional
    public String uploadTempDescriptionImage(MultipartFile imageFile) {
        // 1. 임시 파일 저장 경로 설정 및 생성
        String tempPath = getOsIndependentPath(uploadDir, "temp");
        try {
            Files.createDirectories(Paths.get(tempPath));
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("임시 이미지 폴더 생성에 실패했습니다.", e);
        }

        // 2. 파일 저장
        String storedFileName = fileUtils.saveFile(imageFile, tempPath);

        // 3. 임시 이미지 접근 URL 생성 및 반환 (WebConfig 설정과 일치해야 함)
        // 예: /temp/uuid_image.jpg
        return "/temp/" + storedFileName; // 슬래시 사용
    }

    @Transactional
    public String uploadDescriptionImage(Long sellerUid, Long productId, MultipartFile imageFile) {
        // 1. 상품 소유권 확인
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        if (!product.getSeller().getSellerUid().equals(sellerUid)) {
            throw new AccessDeniedException("해당 상품에 대한 수정 권한이 없습니다.");
        }

        // 2. 파일 저장 경로 설정 및 생성
        String descriptionPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId), "description");
        try {
            Files.createDirectories(Paths.get(descriptionPath));
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("상품 설명 이미지 폴더 생성에 실패했습니다.", e);
        }

        // 3. 파일 저장
        String storedFileName = fileUtils.saveFile(imageFile, descriptionPath);

        // 4. 이미지 접근 URL 생성 및 반환
        // WebConfig에서 /uploads/** 요청을 처리하므로, 그에 맞는 경로를 만들어준다.
        // 예: /product/1/description/uuid_image.jpg
        // 프론트엔드에서는 서버 주소(http://localhost:9090)와 이 경로를 조합하여 사용합니다.
        String imageUrl = "/product/" + productId + "/description/" + storedFileName; // 슬래시 사용
        return imageUrl;
    }

    @Transactional
    public void softDeleteProduct(Long sellerUid, Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        if (!product.getSeller().getSellerUid().equals(sellerUid)) {
            throw new AccessDeniedException("해당 상품에 대한 삭제 권한이 없습니다.");
        }

        product.setIsDeleted(true);
        productRepository.save(product);
        // 실제 파일 삭제는 하지 않음 (복구 가능성을 위해)
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
