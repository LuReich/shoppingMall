package it.back.seller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SellerPublicDTO {

    private Long sellerUid;
    private String companyName;
    private String sellerEmail;
    private LocalDateTime createAt;
    private Boolean isVerified;
    private Boolean isActive;
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;

    public void setVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
