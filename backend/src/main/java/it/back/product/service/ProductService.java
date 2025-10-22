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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CategoryService categoryService;

    public Page<ProductEntity> getAllProducts(Pageable pageable, Integer categoryId) {
        if (categoryId == null) {
            return productRepository.findAll(pageable);
        }
        List<Integer> categoryIds = categoryService.getCategoryWithChild(categoryId);
        return productRepository.findByCategoryId(categoryIds, pageable);
    }

    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
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

    public Optional<ProductDetailEntity> getProductDetail(Long productId) {
        return productDetailRepository.findById(productId);
    }

    public ProductDetailEntity saveProductDetail(ProductDetailEntity detail) {
        return productDetailRepository.save(detail);
    }
}
