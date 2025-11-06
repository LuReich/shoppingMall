package it.back.seller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SellerPublicDTO {

    private Long sellerUid;
    private String companyName;
    private String sellerEmail;
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public void setVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
