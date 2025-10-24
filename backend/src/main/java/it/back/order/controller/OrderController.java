package it.back.order.controller;

import it.back.order.dto.OrderDTO;
import it.back.order.entity.OrderEntity;
import it.back.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 로그인 한 buyer의 주문 목록 조회 (Authentication 기반)
    @GetMapping("/buyer/me")
    public ResponseEntity<List<OrderDTO>> getOrdersByBuyer(Authentication authentication) {
        // 인증 정보에서 buyerUid 추출
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long buyerUid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue()
                : (Long) details.get("uid");

        return ResponseEntity.ok(orderService.getOrdersByBuyerUid(buyerUid));
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
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, Authentication authentication) {

        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long buyerUid = details.get("uid") instanceof Integer ? ((Integer) details.get("uid")).longValue()
                : (Long) details.get("uid");
        orderDTO.setBuyerUid(buyerUid);
        orderService.createOrder(orderDTO);

        return ResponseEntity.ok().build();
    }
}
