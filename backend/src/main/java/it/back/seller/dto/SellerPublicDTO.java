package it.back.seller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SellerPublicDTO {
    private Long sellerUid;
    private String companyName;
    private String sellerEmail;
    private LocalDateTime createAt;
    private boolean isVerified;
    private boolean isActive;
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;
}
