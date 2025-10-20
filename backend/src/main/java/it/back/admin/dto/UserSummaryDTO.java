package it.back.admin.dto;

import it.back.admin.entity.Admin;
import it.back.buyer.entity.Buyer;
import it.back.seller.entity.Seller;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDTO {
    private String role;
    private Long uid;
    private String loginId;
    private String name;

    public UserSummaryDTO(Admin admin) {
        this.role = "ADMIN";
        this.uid = admin.getAdminUid().longValue();
        this.loginId = admin.getAdminId();
        this.name = admin.getAdminName();
    }

    public UserSummaryDTO(Buyer buyer) {
        this.role = "BUYER";
        this.uid = buyer.getBuyerUid();
        this.loginId = buyer.getBuyerId();
        this.name = buyer.getNickname(); // Buyer has nickname
    }

    public UserSummaryDTO(Seller seller) {
        this.role = "SELLER";
        this.uid = seller.getSellerUid();
        this.loginId = seller.getSellerId();
        this.name = seller.getCompanyName(); // Seller has company name
    }
}
