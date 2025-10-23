package it.back.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {
    private String sellerId;
    private String password;
    private String companyName;
    private Boolean isVerified;
    private Boolean isActive;
    // SellerDetail info
    private String businessRegistrationNumber;
    private String phone;
    private String address;
    private String addressDetail;
    private String companyInfo; // 업체 상세 소개 (선택 입력)
        private String sellerEmail;
}
