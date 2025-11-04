package it.back.order.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailSellerResponseDTO {
    private Long orderDetailId;

    // Product Info (for seller's own products)
    private Long productId;
    private String productName;
    private String companyName;

    private Integer quantity;
    private Integer pricePerItem;

    private String productThumbnailUrl;

    // Recipient Info from OrderEntity
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientAddressDetail;

    private String orderDetailStatus;
    private String orderDetailStatusReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;
}
