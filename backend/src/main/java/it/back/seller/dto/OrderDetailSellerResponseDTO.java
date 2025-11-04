package it.back.seller.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailSellerResponseDTO {
    private Long orderDetailId;
    private Integer quantity;
    private Integer pricePerItem;

    // Product Info (for seller's own products)
    private String productName;
    private String productThumbnailUrl;
    private String companyName;

    // Recipient Info from OrderEntity
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientAddressDetail;

    private String orderDetailStatus;
    private String statusReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;
}
