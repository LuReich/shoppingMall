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
import it.back.product.dto.ProductDeletedByAdminRequestDTO;
import it.back.product.dto.ProductDeletedBySellerRequestDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.dto.ProductListDTO;
import it.back.product.dto.ProductUpdateRequestDTO;
import it.back.product.dto.ProductUpdateResponseDTO;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.entity.ProductImageEntity;
import it.back.product.repository.ProductDetailRepository;
import it.back.product.repository.ProductImageRepository;
import it.back.product.repository.ProductLikeRepository;
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
    private final ProductLikeRepository productLikeRepository;
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

    public PageResponseDTO<ProductListDTO> getAllProducts(PageRequestDTO pageRequestDTO, Integer categoryId, String productName, String companyName, Long productId) {

        Pageable pageable = pageRequestDTO.toPageable();

        Specification<ProductEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Add fetch join for Seller to avoid N+1 queries
        spec = spec.and(ProductSpecifications.withSeller());

        spec = spec.and(ProductSpecifications.nameContains(productName));
        spec = spec.and(ProductSpecifications.companyNameContains(companyName));
        spec = spec.and(ProductSpecifications.productIdEquals(productId));

        if (categoryId != null) {
            List<Integer> categoryIds = categoryService.getCategoryWithChild(categoryId);
            spec = spec.and(ProductSpecifications.inCategory(categoryIds));
        }

        Page<ProductEntity> page = productRepository.findAll(spec, pageable);

        List<ProductListDTO> dtos = page.getContent().stream()
                .map(ProductListDTO::new)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(page, dtos);
    }

    // 판매자별 상품 목록 조회
    public PageResponseDTO<ProductListDTO> getProductsBySeller(Long sellerUid, PageRequestDTO pageRequestDTO, Integer categoryId, String productName, Long productId) {
        Pageable pageable = pageRequestDTO.toPageable();

        Specification<ProductEntity> spec = (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("seller").get("sellerUid"), sellerUid);

        // Seller fetch join
        spec = spec.and(ProductSpecifications.withSeller());

        // 상품명 검색 (공백 무시)
        spec = spec.and(ProductSpecifications.nameContains(productName));

        // 상품 ID 검색
        spec = spec.and(ProductSpecifications.productIdEquals(productId));

        // 카테고리 검색
        if (categoryId != null) {
            List<Integer> categoryIds = categoryService.getCategoryWithChild(categoryId);
            spec = spec.and(ProductSpecifications.inCategory(categoryIds));
        }

        Page<ProductEntity> page = productRepository.findAll(spec, pageable);

        List<ProductListDTO> dtos = page.getContent().stream()
                .map(ProductListDTO::new)
                .collect(Collectors.toList());

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
            List<MultipartFile> subImages, List<MultipartFile> description) {
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
        if (description != null && !description.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(descriptionPath));
                for (MultipartFile imageFile : description) {
                    if (imageFile.isEmpty()) {
                        continue;
                    }
                    String storedName = fileUtils.saveFile(imageFile, descriptionPath);
                    String imageUrl = "/product/" + productId + "/description/" + storedName; // 경로 복원
                    descriptionImageUrls.add(imageUrl);
                }
            } catch (IOException e) {
                throw new java.io.UncheckedIOException("상품 설명 이미지 저장 중 오류가 발생했습니다.", e);
            }
        }

        // 2-2. 상품 상세 정보 저장 (product_detail)
        ProductDetailEntity detail = new ProductDetailEntity();
        detail.setProduct(savedProduct); // 연관관계 설정
        // description HTML의 data-image-id를 실제 저장된 URL로 교체
        detail.setDescription(replaceImageIdsWithUrls(dto.getDescription(), dto.getImageMapping(), descriptionImageUrls));
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
        String mainImageUrl = "/product/" + productId + "/main/" + mainImageStoredName; // 경로 복원
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
                String subImageUrl = "/product/" + productId + "/sub/" + storedName; // 경로 복원

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

    @Transactional(readOnly = true)
    public ProductUpdateResponseDTO getProductForUpdate(Long sellerUid, Long productId) {
        // 1. 상품 조회 (이미지 포함)
        ProductEntity product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        // 2. 권한 확인
        if (!product.getSeller().getSellerUid().equals(sellerUid)) {
            throw new AccessDeniedException("해당 상품에 대한 수정 권한이 없습니다.");
        }

        // 3. ProductUpdateResponseDTO로 변환하여 반환
        return new ProductUpdateResponseDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long sellerUid, Long productId, ProductUpdateRequestDTO dto,
            MultipartFile mainImage, List<MultipartFile> subImages, List<MultipartFile> description) {
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

        // 3-1. description의 새 이미지 처리 (data-image-id 방식)
        if (dto.getDescription() != null) {
            // 기존 description의 이미지 URL 목록 추출 (orphan 삭제를 위해)
            List<String> oldImageUrls = extractImageUrlsFromHtml(detail.getDescription());

            String descriptionPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId), "description");
            List<String> newDescriptionImageUrls = new ArrayList<>();

            // 새 이미지가 있으면 저장
            if (description != null && !description.isEmpty()) {
                try {
                    Files.createDirectories(Paths.get(descriptionPath));
                    for (MultipartFile imageFile : description) {
                        if (imageFile.isEmpty()) {
                            continue;
                        }
                        String storedName = fileUtils.saveFile(imageFile, descriptionPath);
                        String imageUrl = "/product/" + productId + "/description/" + storedName; // 경로 복원
                        newDescriptionImageUrls.add(imageUrl);
                    }
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException("상품 설명 이미지 저장 중 오류가 발생했습니다.", e);
                }
            }

            // 기존 description과 새 description을 병합 (data-image-id가 있는 것만 교체)
            String updatedDescription = replaceImageIdsWithUrls(
                    dto.getDescription(),
                    dto.getImageMapping(),
                    newDescriptionImageUrls
            );

            // [안전장치] Base64 이미지를 파일로 변환
            updatedDescription = processBase64Images(updatedDescription, productId);

            detail.setDescription(updatedDescription);

            // 새 description의 이미지 URL 목록 추출 (orphan 삭제를 위해)
            List<String> newImageUrls = extractImageUrlsFromHtml(updatedDescription);

            // 더 이상 사용되지 않는 description 이미지 삭제
            deleteOrphanedDescriptionImages(oldImageUrls, newImageUrls);
        }

        if (dto.getShippingInfo() != null) {
            detail.setShippingInfo(dto.getShippingInfo());
        }
        // productDetailRepository.save(detail)는 productRepository.save(product)에 의해 전파되므로 생략 가능

        // 4. 메인 이미지 업데이트
        if (mainImage != null && !mainImage.isEmpty()) {
            // 기존 메인 이미지 파일 삭제
            if (product.getThumbnailUrl() != null && !product.getThumbnailUrl().isEmpty()) {
                String oldMainImageUrl = product.getThumbnailUrl();
                String relativePath = oldMainImageUrl.startsWith("/") ? oldMainImageUrl.substring(1) : oldMainImageUrl;
                String oldMainImageFullPath = getOsIndependentPath(uploadDir, relativePath);
                fileUtils.deleteFile(oldMainImageFullPath);
            }
            // 새 메인 이미지 저장
            String productBaseDir = getOsIndependentPath(uploadDir, "product", String.valueOf(productId));
            String mainImageDir = getOsIndependentPath(productBaseDir, "main");
            String storedName = fileUtils.saveFile(mainImage, mainImageDir);
            String newMainImageUrlPath = "/product/" + productId + "/main/" + storedName; // 경로 복원
            product.setThumbnailUrl(newMainImageUrlPath);
        }

        // 5. 서브 이미지 업데이트 (JPA orphanRemoval 활용)
        if (dto.getDeleteImageIds() != null && !dto.getDeleteImageIds().isEmpty()) {
            System.out.println("=== 서브 이미지 삭제 시작 ===");
            System.out.println("삭제할 이미지 ID 목록: " + dto.getDeleteImageIds());

            // 5-1. 삭제할 이미지 ID Set 생성
            Set<Long> deleteIds = new java.util.HashSet<>(dto.getDeleteImageIds());

            // 5-2. product가 관리하는 이미지 목록에서 삭제 대상을 찾아 제거
            List<ProductImageEntity> imagesToDelete = product.getProductImages().stream()
                    .filter(img -> deleteIds.contains(img.getImageId()))
                    .collect(Collectors.toList());

            System.out.println("삭제할 이미지 엔티티 수: " + imagesToDelete.size());

            // 5-3. 파일 시스템에서 실제 파일 삭제
            for (ProductImageEntity image : imagesToDelete) {
                System.out.println("이미지 경로(DB): " + image.getImagePath());

                String relativePath = image.getImagePath().startsWith("/")
                        ? image.getImagePath().substring(1)
                        : image.getImagePath();

                String fullPath = getOsIndependentPath(uploadDir, relativePath);
                System.out.println("파일 삭제 시도 - 전체 경로: " + fullPath);

                File fileToDelete = new File(fullPath);
                System.out.println("파일 존재 여부: " + fileToDelete.exists());

                if (fileToDelete.exists()) {
                    fileUtils.deleteFile(fullPath);
                    System.out.println("파일 삭제 완료: " + fullPath);
                } else {
                    System.out.println("파일이 존재하지 않음: " + fullPath);
                }
            }

            // 5-4. 컬렉션에서 제거 -> orphanRemoval=true에 의해 DB에서도 삭제됨
            product.getProductImages().removeAll(imagesToDelete);
            System.out.println("=== 서브 이미지 삭제 완료 ===");
        }

        // 6. 새 서브 이미지 추가
        if (subImages != null && !subImages.isEmpty()) {
            // 현재 남아있는 이미지들의 최대 정렬 순서를 찾음
            int maxSortOrder = product.getProductImages().stream()
                    .mapToInt(ProductImageEntity::getSortOrder)
                    .max().orElse(-1);
            int currentSortOrder = maxSortOrder + 1;

            String productBaseDir = getOsIndependentPath(uploadDir, "product", String.valueOf(productId));
            String subImageDir = getOsIndependentPath(productBaseDir, "sub");

            for (MultipartFile file : subImages) {
                if (file.isEmpty()) {
                    continue;
                }

                String storedName = fileUtils.saveFile(file, subImageDir);
                String newSubImageUrl = "/product/" + productId + "/sub/" + storedName; // 경로 복원
                ProductImageEntity imageEntity = ProductImageEntity.builder()
                        .product(product) // 연관관계의 주인에게 자신을 설정
                        .imageName(file.getOriginalFilename()).storedName(storedName)
                        .imagePath(newSubImageUrl).imageSize(file.getSize()).sortOrder(currentSortOrder++).build();

                // 컬렉션에 추가 -> CascadeType.ALL에 의해 DB에도 저장됨
                product.getProductImages().add(imageEntity);
            }
        }

        productRepository.save(product); // 변경된 product 엔티티 저장 (이미지 삭제/추가 모두 반영)
        // 모든 정보가 포함된 완전한 Entity를 다시 조회하여 DTO로 변환 후 반환
        return new ProductDTO(productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("업데이트된 상품을 다시 찾을 수 없습니다. ID: " + productId)));
    }

    /**
     * description HTML의 data-image-id를 실제 저장된 URL로 교체합니다.
     *
     * @param htmlContent description HTML (data-image-id 속성 포함)
     * @param imageMapping 이미지 ID 배열 (순서대로)
     * @param descriptionImageUrls 실제 저장된 이미지 URL 배열
     * @return URL이 교체된 HTML 문자열
     */
    private String replaceImageIdsWithUrls(String htmlContent, List<String> imageMapping, List<String> descriptionImageUrls) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return htmlContent;
        }
        if (imageMapping == null || imageMapping.isEmpty()) {
            return htmlContent; // imageMapping이 없으면 원본 그대로 반환
        }

        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img[data-image-id]");

        for (Element img : images) {
            String imageId = img.attr("data-image-id");

            // imageMapping에서 해당 ID의 인덱스를 찾음
            int index = imageMapping.indexOf(imageId);

            if (index >= 0 && index < descriptionImageUrls.size()) {
                // Blob URL을 실제 저장된 URL로 교체
                img.attr("src", descriptionImageUrls.get(index));
                // data-image-id 속성 제거 (더 이상 필요 없음)
                img.removeAttr("data-image-id");
            }
        }

        return doc.body().html();
    }

    /**
     * 기존 방식: description HTML에서 blob: URL이나 임시 이미지 경로를 실제 저장된 이미지 URL로 교체합니다.
     *
     * @deprecated data-image-id 방식으로 대체됨
     */
    @Deprecated
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
                String permanentUrl = "/product/" + productId + "/description/" + tempFileName; // 경로 복원
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

    @Transactional
    public ProductDTO softDeleteProduct(Long sellerUid, Long productId, ProductDeletedBySellerRequestDTO requestDTO) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        if (!product.getSeller().getSellerUid().equals(sellerUid)) {
            throw new AccessDeniedException("해당 상품에 대한 삭제 권한이 없습니다.");
        }

        product.setIsDeleted(true);
        product.setDeletedBySellerReason(requestDTO.getDeletedBySellerReason());
        product.setDeletedByAdminReason(null); // 판매자가 삭제 시, 기존 관리자 사유는 초기화
        ProductEntity updatedProduct = productRepository.save(product);
        return new ProductDTO(updatedProduct);
        // 실제 파일 삭제는 하지 않음 (복구 가능성을 위해)
    }

    @Transactional
    public ProductDTO updateProductDeletionStatusByAdmin(Long productId, ProductDeletedByAdminRequestDTO requestDTO) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.setIsDeleted(requestDTO.getIsDeleted());
        product.setDeletedByAdminReason(requestDTO.getDeletedByAdminReason());

        // 관리자가 판매자 삭제 사유를 직접 설정하는 경우
        if (requestDTO.getDeletedBySellerReason() != null) {
            product.setDeletedBySellerReason(requestDTO.getDeletedBySellerReason());
        } else if (!requestDTO.getIsDeleted()) {
            // 상품이 복구되고, 관리자가 별도 사유를 입력하지 않은 경우 판매자 삭제 사유 초기화
            product.setDeletedBySellerReason(null);
        }

        ProductEntity updatedProduct = productRepository.save(product);
        return new ProductDTO(updatedProduct);
    }

    public void deleteProduct(Long id, String reason) {
        productRepository.findById(id).ifPresent(product -> {
            product.setIsDeleted(true);
            product.setDeletedByAdminReason(reason);
            productRepository.save(product);
        });
    }

    public ProductDetailDTO getProductDetail(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        ProductDetailEntity detail = product.getProductDetail();
        if (detail == null) {
            return null; // 상세 정보가 없는 경우
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(detail.getProductId());
        dto.setDescription(detail.getDescription());
        dto.setShippingInfo(detail.getShippingInfo());
        dto.setLikeCount(product.getLikeCount()); // Entity에서 직접 가져옴
        dto.setAverageRating(product.getAverageRating()); // Entity에서 직접 가져옴
        dto.setIsDeleted(product.getIsDeleted());
        dto.setDeletedByAdminReason(product.getDeletedByAdminReason());
        dto.setDeletedBySellerReason(product.getDeletedBySellerReason());
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

    /**
     * [안전장치] HTML 컨텐츠에 포함된 Base64 인코딩된 이미지를 디코딩하여 파일로 저장하고, src 속성을 실제 URL로
     * 교체합니다.
     *
     * @param htmlContent Base64 이미지가 포함될 수 있는 HTML 문자열
     * @param productId 상품 ID
     * @return Base64 이미지가 실제 URL로 대체된 HTML 문자열
     */
    private String processBase64Images(String htmlContent, Long productId) {
        if (htmlContent == null || !htmlContent.contains("data:image")) {
            return htmlContent;
        }

        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img[src^=data:image]");

        if (images.isEmpty()) {
            return htmlContent;
        }

        String descriptionPath = getOsIndependentPath(uploadDir, "product", String.valueOf(productId), "description");
        try {
            Files.createDirectories(Paths.get(descriptionPath));
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("상품 설명 이미지 폴더 생성에 실패했습니다.", e);
        }

        for (Element img : images) {
            String base64Src = img.attr("src");
            String[] parts = base64Src.split(",");
            if (parts.length != 2) {
                continue; // 잘못된 형식의 Base64 데이터
            }

            // ex: data:image/png;base64
            String mimeType = parts[0].substring(parts[0].indexOf(':') + 1, parts[0].indexOf(';'));
            String extension = mimeType.substring(mimeType.indexOf('/') + 1);
            byte[] imageBytes = java.util.Base64.getDecoder().decode(parts[1]);

            String storedName = java.util.UUID.randomUUID().toString() + "." + extension;
            Path destinationPath = Paths.get(descriptionPath, storedName);

            try {
                Files.write(destinationPath, imageBytes);
            } catch (IOException e) {
                // 개별 이미지 저장 실패 시, 해당 이미지는 변환하지 않고 넘어가도록 처리 (전체 프로세스 중단 방지)
                e.printStackTrace();
                continue;
            }

            String newUrl = "/product/" + productId + "/description/" + storedName;
            img.attr("src", newUrl);
        }

        return doc.body().html();
    }
}
