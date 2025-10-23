package it.back.cart.service;

import it.back.cart.dto.CartDTO;
import it.back.cart.dto.CartItemResponseDTO;
import it.back.cart.entity.CartEntity;
import it.back.cart.repository.CartRepository;
import it.back.buyer.entity.BuyerEntity;
import it.back.product.entity.ProductEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponseDTO> getCartList(Long buyerUid) {
        return cartRepository.findByBuyer_BuyerUid(buyerUid).stream().map(entity -> {
            CartItemResponseDTO dto = new CartItemResponseDTO();
            dto.setCartId(entity.getCartId());
            dto.setProductId(entity.getProduct().getProductId());
            dto.setProductName(entity.getProduct().getProductName());
            dto.setThumbnailUrl(entity.getProduct().getThumbnailUrl());
            dto.setQuantity(entity.getQuantity());
            dto.setPricePerItem(entity.getProduct().getPrice());
            dto.setSellerCompanyName(entity.getProduct().getSeller().getCompanyName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addToCart(Long buyerUid, Long productId, Integer quantity) {
        BuyerEntity buyer = buyerRepository.findById(buyerUid).orElseThrow();
        ProductEntity product = productRepository.findById(productId).orElseThrow();
        CartEntity cart = cartRepository.findByBuyer_BuyerUidAndProduct_ProductId(buyerUid, productId);
        int newQuantity = (cart != null) ? cart.getQuantity() + quantity : quantity;
        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("요청 수량이 상품 재고(" + product.getStock() + ")를 초과할 수 없습니다.");
        }
        if (cart != null) {
            cart.setQuantity(newQuantity);
            cartRepository.save(cart);
        } else {
            cart = new CartEntity();
            cart.setBuyer(buyer);
            cart.setProduct(product);
            cart.setQuantity(newQuantity);
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void updateCartQuantity(Long cartId, Integer quantity, Long buyerUid) {
        CartEntity cart = cartRepository.findById(cartId).orElseThrow();
        if (!cart.getBuyer().getBuyerUid().equals(buyerUid)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        ProductEntity product = cart.getProduct();
        int newQuantity = quantity;
        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("요청 수량이 상품 재고(" + product.getStock() + ")를 초과할 수 없습니다.");
        }
        cart.setQuantity(newQuantity);
    }

    @Transactional
    public void removeFromCart(Long cartId, Long buyerUid) {
        CartEntity cart = cartRepository.findById(cartId).orElseThrow();
        if (!cart.getBuyer().getBuyerUid().equals(buyerUid)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        cartRepository.delete(cart);
    }

    @Transactional
    public void removeFromCartAtOnce(List<Long> cartIds, Long buyerUid) {
        List<CartEntity> carts = cartRepository.findAllById(cartIds);
        for (CartEntity cart : carts) {
            if (!cart.getBuyer().getBuyerUid().equals(buyerUid)) {
                throw new IllegalArgumentException("권한이 없습니다. (cartId: " + cart.getCartId() + ")");
            }
        }
        cartRepository.deleteAll(carts);
    }
}
