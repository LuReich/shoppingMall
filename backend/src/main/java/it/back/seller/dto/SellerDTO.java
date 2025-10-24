package it.back.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {

    private Long sellerUid;
    private String sellerId;
    private String sellerEmail;
    private String companyName;
    private Boolean isVerified;
    private Boolean isActive;
    private String withdrawalStatus;
    private String withdrawalReason;
    private java.time.LocalDateTime createAt;
    private java.time.LocalDateTime updateAt;
    // SellerDetail info
    private String businessRegistrationNumber;
    private String phone;
    private String address;
    private String addressDetail;
    private String companyInfo;
}
