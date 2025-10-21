package it.back.product.service;

import it.back.product.dto.ProductDTO;
import it.back.product.dto.ProductDetailDTO;
import it.back.product.entity.ProductEntity;
import it.back.product.entity.ProductDetailEntity;
import it.back.product.repository.ProductRepository;
import it.back.product.repository.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<ProductDetailEntity> getProductDetail(Long productId) {
        return productDetailRepository.findById(productId);
    }

    public ProductDetailEntity saveProductDetail(ProductDetailEntity detail) {
        return productDetailRepository.save(detail);
    }
}
