package it.back.order.controller;

import it.back.common.dto.ApiResponse;
import it.back.order.dto.OrderResponseDTO;
import it.back.order.dto.OrderDTO;
import it.back.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import it.back.common.pagination.PageResponseDTO;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 인증 정보에서 buyerUid 추출 및 BUYER 권한 체크
    private Long getBuyerUid(Authentication authentication) {
        Object detailsObj = authentication.getDetails();
        if (!(detailsObj instanceof Map<?, ?> details)) {
            throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
        }
        Object roleObj = details.get("role");
        if (roleObj == null || !"BUYER".equals(roleObj.toString())) {
            throw new IllegalStateException("구매자(BUYER) 권한이 필요합니다.");
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

    // 로그인 한 buyer의 주문 목록 조회 (Authentication 기반)
    @GetMapping("/buyer/me")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderResponseDTO>>> getOrdersByBuyer(
            Authentication authentication,
            Pageable pageable) {
        Long buyerUid = getBuyerUid(authentication);
        PageResponseDTO<OrderResponseDTO> result = orderService.getOrdersByBuyerUid(buyerUid, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }

    // 주문 생성 시 buyerUid를 인증에서 추출해 DTO에 세팅
    // {
    // "buyerUid": 1,
    // "totalPrice": 55000,
    // "recipientName": "김철수",
    // "recipientAddress": "서울특별시 강남구 테헤란로 123",
    // "recipientAddressDetail": "101호",
    // "orderStatus": "PAID",
    // "orderDetails": [
    // {
    // "productId": 3,
    // "sellerUid": 2,
    // "quantity": 1,
    // "pricePerItem": 55000,
    // "orderDetailStatus": "PAID"
    // },
    // {
    // "productId": 1,
    // "sellerUid": 2,
    // "quantity": 1,
    // "pricePerItem": 55000,
    // "orderDetailStatus": "PAID"
    // }
    // ]
    // }

    // 주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody OrderDTO orderDTO,
            Authentication authentication) {
        Long buyerUid = getBuyerUid(authentication);
        orderDTO.setBuyerUid(buyerUid);
        OrderResponseDTO response = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }
}
