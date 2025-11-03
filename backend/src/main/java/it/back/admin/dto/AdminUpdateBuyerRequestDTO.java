package it.back.admin.dto;

import java.time.LocalDate;

import it.back.buyer.entity.BuyerEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateBuyerRequestDTO {

    private String buyerId; // 관리자는 아이디도 수정 가능
    
    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String password;
    private String nickname;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String buyerEmail;
    
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private String gender; // enum 사용 시 Enum 타입으로 변경

    private Boolean isActive; // 관리자는 활성화 상태 변경 가능
    private BuyerEntity.WithdrawalStatus withdrawalStatus; // 관리자는 탈퇴 상태 변경 가능
    private String withdrawalReason; // 관리자는 탈퇴 사유 변경 가능
}