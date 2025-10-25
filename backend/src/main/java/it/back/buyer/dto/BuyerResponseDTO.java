package it.back.buyer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.back.buyer.entity.BuyerDetailEntity;
import it.back.buyer.entity.BuyerEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerResponseDTO {

    // 기본 생성자 (에러 메시지 전달용)
    public BuyerResponseDTO() {
    }

    public void setErrorMessage(String msg) {
        this.nickname = msg;
    }
    // nickname 필드에 에러 메시지 전달(프론트에서 분기 처리)

    private Long buyerUid;
    private String buyerId;
    private String buyerEmail;
    private String nickname;
    private String phone;
    private String address;
    private String addressDetail;
    private LocalDate birth;
    private BuyerDetailEntity.Gender gender;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonProperty("isActive")
    private Boolean isActive;
    // 필요시 Buyer 엔티티의 모든 필드 추가

    public BuyerResponseDTO(BuyerEntity buyer) {
        this.buyerUid = buyer.getBuyerUid();
        this.buyerId = buyer.getBuyerId();
        this.buyerEmail = buyer.getBuyerEmail();
        this.nickname = buyer.getNickname();

        if (buyer.getBuyerDetail() != null) {
            this.phone = buyer.getBuyerDetail().getPhone();
            this.address = buyer.getBuyerDetail().getAddress();
            this.addressDetail = buyer.getBuyerDetail().getAddressDetail();
            this.birth = buyer.getBuyerDetail().getBirth();
            this.gender = buyer.getBuyerDetail().getGender();
        }
        this.createAt = buyer.getCreateAt();
        this.updateAt = buyer.getUpdateAt();
        this.isActive = buyer.isActive();
    }
}
