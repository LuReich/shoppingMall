package it.back.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateMeRequestDTO {

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String adminName;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String adminEmail;

    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String password;

}