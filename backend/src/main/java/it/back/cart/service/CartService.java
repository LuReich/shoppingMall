package it.back.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.cart.dto.CartDTO;
import it.back.cart.dto.CartItemResponseDTO;
import it.back.cart.entity.CartEntity;
import it.back.cart.repository.CartRepository;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional

public class CartService {

    private static final int MAX_CART_QUANTITY = 50;

    private final CartRepository cartRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    // 장바구니 추가
    public CartItemResponseDTO addCartItem(long buyerId, CartDTO cartDTO) {
        if (cartDTO.getQuantity() < 1) {
            throw new IllegalArgumentException("장바구니에는 최소 1개 이상 담아야 합니다.");
        }
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ProductEntity product = productRepository.findById(cartDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cartEntity = cartRepository.findByBuyerAndProduct(buyer, product)
                .orElse(null);

        int newQuantity = cartDTO.getQuantity();
        String message = null;
        if (cartEntity != null) {
            newQuantity = cartEntity.getQuantity() + cartDTO.getQuantity();
        }
        if (newQuantity > MAX_CART_QUANTITY) {
            newQuantity = MAX_CART_QUANTITY;
            message = "장바구니에는 최대 " + MAX_CART_QUANTITY + "개까지만 담을 수 있어, 최대치로 조정되었습니다.";
        }

        CartEntity savedEntity;
        if (cartEntity != null) {
            cartEntity.setQuantity(newQuantity);
            savedEntity = cartRepository.save(cartEntity);
        } else {
            CartEntity newCartEntity = new CartEntity();
            newCartEntity.setBuyer(buyer);
            newCartEntity.setProduct(product);
            newCartEntity.setQuantity(newQuantity);
            savedEntity = cartRepository.save(newCartEntity);
        }
        cartRepository.flush();

        // DB에서 재조회하여 createdAt, updatedAt 보장
        CartEntity refreshed = cartRepository.findById(savedEntity.getCartId())
                .orElse(savedEntity);

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartId(refreshed.getCartId());
        dto.setProductId(refreshed.getProduct().getProductId());
        dto.setProductName(refreshed.getProduct().getProductName());
        dto.setThumbnailUrl(refreshed.getProduct().getThumbnailUrl());
        dto.setQuantity(refreshed.getQuantity());
        dto.setPricePerItem(refreshed.getProduct().getPrice());
        dto.setSellerCompanyName(refreshed.getProduct().getSeller().getCompanyName());
        dto.setCreatedAt(refreshed.getCreatedAt());
        dto.setUpdatedAt(refreshed.getUpdatedAt());
        if (message != null) {
            dto.setMessage(message);
        }
        return dto;
    }

    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<CartItemResponseDTO> getCartList(long buyerId, Pageable pageable) {
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<CartEntity> cartPage = cartRepository.findByBuyer(buyer, pageable);

        List<CartItemResponseDTO> dtoList = cartPage.getContent().stream()
                .map(cartEntity -> {
                    CartItemResponseDTO dto = new CartItemResponseDTO();
                    dto.setCartId(cartEntity.getCartId());
                    dto.setProductId(cartEntity.getProduct().getProductId());
                    dto.setProductName(cartEntity.getProduct().getProductName());
                    dto.setThumbnailUrl(cartEntity.getProduct().getThumbnailUrl());
                    dto.setQuantity(cartEntity.getQuantity());
                    dto.setPricePerItem(cartEntity.getProduct().getPrice());
                    dto.setSellerCompanyName(cartEntity.getProduct().getSeller().getCompanyName());
                    dto.setCreatedAt(cartEntity.getCreatedAt());
                    dto.setUpdatedAt(cartEntity.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                dtoList,
                cartPage.getNumber(),
                cartPage.getSize(),
                cartPage.getTotalElements(),
                cartPage.getTotalPages(),
                cartPage.isLast()
        );
    }

    // 장바구니 수량 변경
    public CartItemResponseDTO updateQuantity(long cartId, int quantity, long buyerId) {
        if (quantity < 1) {
            throw new IllegalArgumentException("장바구니에는 최소 1개 이상 담아야 합니다.");
        }
        CartEntity cartEntity = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
        if (cartEntity.getBuyer().getBuyerUid() != buyerId) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        String message = null;
        int newQuantity = quantity;
        if (quantity > MAX_CART_QUANTITY) {
            newQuantity = MAX_CART_QUANTITY;
            message = "장바구니에는 최대 " + MAX_CART_QUANTITY + "개까지만 담을 수 있어, 최대치로 조정되었습니다.";
        }
        cartEntity.setQuantity(newQuantity);
        CartEntity updatedEntity = cartRepository.save(cartEntity);
        cartRepository.flush();

        // DB에서 재조회하여 createdAt, updatedAt 보장
        CartEntity refreshed = cartRepository.findById(updatedEntity.getCartId())
                .orElse(updatedEntity);

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartId(refreshed.getCartId());
        dto.setProductId(refreshed.getProduct().getProductId());
        dto.setProductName(refreshed.getProduct().getProductName());
        dto.setThumbnailUrl(refreshed.getProduct().getThumbnailUrl());
        dto.setQuantity(refreshed.getQuantity());
        dto.setPricePerItem(refreshed.getProduct().getPrice());
        dto.setSellerCompanyName(refreshed.getProduct().getSeller().getCompanyName());
        dto.setCreatedAt(refreshed.getCreatedAt());
        dto.setUpdatedAt(refreshed.getUpdatedAt());
        if (message != null) {
            dto.setMessage(message);
        }
        return dto;
    }

    // 장바구니 개별 삭제
    public String deleteCartItem(long cartId, long buyerId) {
        CartEntity cartEntity = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
        if (cartEntity.getBuyer().getBuyerUid() != buyerId) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        cartRepository.delete(cartEntity);
        return "상품이 장바구니에서 삭제되었습니다.";
    }

    // 장바구니 선택 삭제
    public String deleteSelectedCartItems(List<Long> cartIds, long buyerId) {
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        cartRepository.deleteByIdInAndBuyer(cartIds, buyer);
        return cartIds.size() + "개의 상품이 장바구니에서 삭제되었습니다.";
    }
}
