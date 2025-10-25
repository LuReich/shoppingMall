package it.back.cart.service;

import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.cart.dto.CartDTO;
import it.back.cart.dto.CartItemResponseDTO;
import it.back.cart.entity.CartEntity;
import it.back.cart.repository.CartRepository;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    // 장바구니 추가
    public CartItemResponseDTO addCartItem(long buyerId, CartDTO cartDTO) {
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ProductEntity product = productRepository.findById(cartDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cartEntity = cartRepository.findByBuyerAndProduct(buyer, product)
                .orElse(null);

        CartEntity savedEntity;
        if (cartEntity != null) {
            cartEntity.setQuantity(cartEntity.getQuantity() + cartDTO.getQuantity());
            savedEntity = cartRepository.save(cartEntity);
        } else {
            CartEntity newCartEntity = new CartEntity();
            newCartEntity.setBuyer(buyer);
            newCartEntity.setProduct(product);
            newCartEntity.setQuantity(cartDTO.getQuantity());
            savedEntity = cartRepository.save(newCartEntity);
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartId(savedEntity.getCartId());
        dto.setProductId(savedEntity.getProduct().getProductId());
        dto.setProductName(savedEntity.getProduct().getProductName());
        dto.setThumbnailUrl(savedEntity.getProduct().getThumbnailUrl());
        dto.setQuantity(savedEntity.getQuantity());
        dto.setPricePerItem(savedEntity.getProduct().getPrice());
        dto.setSellerCompanyName(savedEntity.getProduct().getSeller().getCompanyName());
        return dto;
    }

    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<CartItemResponseDTO> getCartList(long buyerId, PageRequestDTO pageRequestDTO) {
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage(), pageRequestDTO.getSize(), Sort.by("cartId").descending());
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
        CartEntity cartEntity = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
        if (cartEntity.getBuyer().getBuyerUid() != buyerId) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        cartEntity.setQuantity(quantity);
        CartEntity updatedEntity = cartRepository.save(cartEntity);

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartId(updatedEntity.getCartId());
        dto.setProductId(updatedEntity.getProduct().getProductId());
        dto.setProductName(updatedEntity.getProduct().getProductName());
        dto.setThumbnailUrl(updatedEntity.getProduct().getThumbnailUrl());
        dto.setQuantity(updatedEntity.getQuantity());
        dto.setPricePerItem(updatedEntity.getProduct().getPrice());
        dto.setSellerCompanyName(updatedEntity.getProduct().getSeller().getCompanyName());
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