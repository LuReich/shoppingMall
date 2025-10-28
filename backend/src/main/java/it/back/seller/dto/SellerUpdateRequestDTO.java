package it.back.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerUpdateRequestDTO {

    private String password; // 비밀번호 변경 시에만 값 전달, 아니면 null/blank
    private String companyName;
    private String sellerEmail;
    private String businessRegistrationNumber;
    private String phone;
    private String address;
    private String addressDetail;
    private String companyInfo;

}
