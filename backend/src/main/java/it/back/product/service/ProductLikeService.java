package it.back.product.service;

import it.back.buyer.entity.BuyerEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.entity.ProductLikeEntity;
import it.back.product.repository.ProductLikeRepository;
import it.back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository; // ProductRepository 주입

    public boolean toggleLike(long buyerUid, long productId) {
        Optional<ProductLikeEntity> like = productLikeRepository.findByBuyer_BuyerUidAndProduct_ProductId(buyerUid, productId);

        if (like.isPresent()) {
            productLikeRepository.delete(like.get());
            productRepository.updateLikeCount(productId, -1); // 좋아요 카운트 감소
            return false; // 좋아요 취소
        } else {
            BuyerEntity buyer = new BuyerEntity();
            buyer.setBuyerUid(buyerUid);

            ProductEntity product = new ProductEntity();
            product.setProductId(productId);

            ProductLikeEntity newLike = ProductLikeEntity.builder()
                    .buyer(buyer)
                    .product(product)
                    .build();
            productLikeRepository.save(newLike);
            productRepository.updateLikeCount(productId, 1); // 좋아요 카운트 증가
            return true; // 좋아요 추가
        }
    }
}
