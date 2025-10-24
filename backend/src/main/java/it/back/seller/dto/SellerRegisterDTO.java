package it.back.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerRegisterDTO {

    private String sellerId;
    private String password;
    private String companyName;
    private String sellerEmail;
    private String businessRegistrationNumber;
    private String phone;
    private String address;
    private String addressDetail;
    private String companyInfo;
}
