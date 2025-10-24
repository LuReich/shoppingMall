package it.back.buyer.dto;

import java.time.LocalDate;
import it.back.buyer.entity.BuyerDetailEntity.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerRegisterDTO {

    private String buyerId;
    private String password;
    private String nickname;
    private String buyerEmail;
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private Gender gender;
}
