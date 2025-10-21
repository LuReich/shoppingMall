package it.back.admin.dto;

import it.back.admin.entity.AdminEntity;
import it.back.buyer.entity.BuyerEntity;
import it.back.seller.entity.SellerEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDTO {
    private String role;
    private Long uid;
    private String loginId;
    private String name;

    public UserSummaryDTO(AdminEntity admin) {
        this.role = "ADMIN";
        this.uid = admin.getAdminUid().longValue();
        this.loginId = admin.getAdminId();
        this.name = admin.getAdminName();
    }

    public UserSummaryDTO(BuyerEntity buyer) {
        this.role = "BUYER";
        this.uid = buyer.getBuyerUid();
        this.loginId = buyer.getBuyerId();
        this.name = buyer.getNickname();
    }

    public UserSummaryDTO(SellerEntity seller) {
        this.role = "SELLER";
        this.uid = seller.getSellerUid();
        this.loginId = seller.getSellerId();
        this.name = seller.getCompanyName();
    }
}
