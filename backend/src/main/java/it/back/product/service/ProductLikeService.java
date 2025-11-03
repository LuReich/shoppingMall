package it.back.product.service;

import it.back.buyer.entity.BuyerEntity;
import it.back.product.entity.ProductEntity;
import it.back.product.entity.ProductLikeEntity;
import it.back.product.repository.ProductLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;

    public boolean toggleLike(long buyerUid, long productId) {
        Optional<ProductLikeEntity> like = productLikeRepository.findByBuyer_BuyerUidAndProduct_ProductId(buyerUid, productId);

        if (like.isPresent()) {
            productLikeRepository.delete(like.get());
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
            return true; // 좋아요 추가
        }
    }
}
