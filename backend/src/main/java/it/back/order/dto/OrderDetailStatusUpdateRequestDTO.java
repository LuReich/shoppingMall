package it.back.order.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class OrderDetailStatusUpdateRequestDTO {

    @NotBlank(message = "주문 상세 상태는 필수입니다.")
    private String orderDetailStatus;

    private String orderDetailStatusReason;
}