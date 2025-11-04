package it.back.admin.dto;

import it.back.seller.entity.SellerEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateSellerRequestDTO {

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{4,19}$", message = "아이디는 영문으로 시작해야 하며, 5~20자의 영문 또는 숫자 조합이어야 합니다.")
    private String sellerId; // 관리자는 아이디도 수정 가능

    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String password;
    private String companyName;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String sellerEmail;

    private String phone; // 전화번호 중복 허용

    @Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리 숫자만 입력해야 합니다.")
    private String businessRegistrationNumber; // 사업자등록번호 중복 체크 필요

    private String companyInfo;
    private String address;
    private String addressDetail;

    private Boolean isActive; // 관리자는 활성화 상태 변경 가능
    private Boolean isVerified; // 관리자는 인증 상태 변경 가능
    private SellerEntity.WithdrawalStatus withdrawalStatus; // 관리자는 탈퇴 상태 변경 가능
    private String withdrawalReason; // 관리자는 탈퇴 사유 변경 가능
}
