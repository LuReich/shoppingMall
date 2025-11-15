package it.back.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.admin.dto.AdminOrderDetailResponseDTO;
import it.back.admin.dto.AdminOrderDetailSearchDTO;
import it.back.admin.dto.AdminOrderResponseDTO;
import it.back.admin.dto.AdminOrderSearchDTO;
import it.back.admin.service.AdminOrderService;
import it.back.common.dto.ApiResponse;
import it.back.common.pagination.PageResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/orders/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<AdminOrderResponseDTO>>> searchOrders(
            @ModelAttribute AdminOrderSearchDTO searchDTO) {

        PageResponseDTO<AdminOrderResponseDTO> results = adminOrderService.searchOrders(searchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(results));
    }

    @GetMapping("/orderDetail/list")
    public ResponseEntity<ApiResponse<PageResponseDTO<AdminOrderDetailResponseDTO>>> searchOrderDetails(
            @ModelAttribute AdminOrderDetailSearchDTO searchDTO) {

        PageResponseDTO<AdminOrderDetailResponseDTO> results = adminOrderService.searchOrderDetails(searchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(results));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<AdminOrderResponseDTO>> getOrderById(@PathVariable("orderId") Long orderId) {
        AdminOrderResponseDTO order = adminOrderService.getOrderById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(order));
    }

    @GetMapping("/orderDetail/{orderDetailId}")
    public ResponseEntity<ApiResponse<AdminOrderDetailResponseDTO>> getOrderDetailById(@PathVariable("orderDetailId") Long orderDetailId) {
        AdminOrderDetailResponseDTO orderDetail = adminOrderService.getOrderDetailById(orderDetailId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(orderDetail));
    }
}
