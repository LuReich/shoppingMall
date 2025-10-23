package it.back.buyer.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class BuyerUpdateRequestDTO {
    private String password;
    private String nickname;
    private String buyerEmail;
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private String gender; // enum 사용 시 Enum 타입으로 변경
}
