package it.back.buyer.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerUpdateRequestDTO {

    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String password;
    private String nickname;
    private String buyerEmail;
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private String gender; // enum 사용 시 Enum 타입으로 변경
}
