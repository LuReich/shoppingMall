package it.back.buyer.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerResponseDTO {
    private Long buyerUid;
    private String buyerId;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String addressDetail;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private boolean isActive;
    // 필요시 Buyer 엔티티의 모든 필드 추가

    public BuyerResponseDTO(it.back.buyer.entity.BuyerEntity buyer) {
        this.buyerUid = buyer.getBuyerUid();
        this.buyerId = buyer.getBuyerId();
        this.password = buyer.getPassword();
        this.nickname = buyer.getNickname();
        if (buyer.getBuyerDetail() != null) {
            this.phoneNumber = buyer.getBuyerDetail().getPhoneNumber();
            this.address = buyer.getBuyerDetail().getAddress();
            this.addressDetail = buyer.getBuyerDetail().getAddressDetail();
        }
        this.createAt = buyer.getCreateAt();
        this.updateAt = buyer.getUpdateAt();
        this.isActive = buyer.isActive();
    }
}
