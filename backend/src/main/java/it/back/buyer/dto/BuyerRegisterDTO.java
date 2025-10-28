package it.back.buyer.dto;

import java.time.LocalDate;

import it.back.buyer.entity.BuyerDetailEntity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerRegisterDTO {

    private String buyerId;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String password;
    private String nickname;
    private String buyerEmail;
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private Gender gender;
}
