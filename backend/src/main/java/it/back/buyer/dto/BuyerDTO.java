package it.back.buyer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerDTO {
    private String buyerId;
    private String password;
    private String nickname;
    // BuyerDetail info
    private String phoneNumber;
    private String address;
    private String addressDetail;
}
