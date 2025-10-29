package it.back.order.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.common.dto.ApiResponse;
import it.back.order.dto.OrderDTO;
import it.back.order.dto.OrderResponseDTO;
import it.back.order.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    

    // 주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody OrderDTO orderDTO,
            Authentication authentication) {
        Long buyerUid = getBuyerUid(authentication);
        orderDTO.setBuyerUid(buyerUid);
        OrderResponseDTO response = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

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
}
