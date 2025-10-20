package it.back.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {
    private String userId;
    private String password;
    private String companyName;
    // SellerDetail info
    private String businessRegistrationNumber;
    private String phone;
    private String address;
    private String addressDetail;
}
