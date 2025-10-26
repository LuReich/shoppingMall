package it.back.order.controller;

import it.back.common.dto.ApiResponse;
import it.back.order.dto.OrderDTO;
import it.back.order.entity.OrderEntity;
import it.back.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 인증 정보에서 buyerUid 추출 및 BUYER 권한 체크
    private Long getBuyerUid(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Object roleObj = details.get("role");
        if (roleObj == null || !"BUYER".equals(roleObj.toString())) {
            throw new IllegalStateException("구매자(BUYER) 권한이 필요합니다.");
        }
        Object uidObj = details.get("uid");
        if (uidObj instanceof Integer) {
            return ((Integer) uidObj).longValue();
        }
        return (Long) uidObj;
    }

    // 로그인 한 buyer의 주문 목록 조회 (Authentication 기반)
    @GetMapping("/buyer/me")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByBuyer(
            Authentication authentication) {

        Long buyerUid = getBuyerUid(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(orderService.getOrdersByBuyerUid(buyerUid)));
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
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody OrderDTO orderDTO,
            Authentication authentication) {

        Long buyerUid = getBuyerUid(authentication);
        orderDTO.setBuyerUid(buyerUid);
        orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Order created successfully"));
    }
}
