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
    private Long id;
    private String loginId;
    private String name;

    public UserSummaryDTO(Admin admin) {
        this.role = "ADMIN";
        this.id = admin.getAdminUid().longValue();
        this.loginId = admin.getAdminId();
        this.name = admin.getAdminName();
    }

    public UserSummaryDTO(Buyer buyer) {
        this.role = "BUYER";
        this.id = buyer.getBuyerUid();
        this.loginId = buyer.getUserId();
        this.name = buyer.getNickname(); // Buyer has nickname
    }

    public UserSummaryDTO(Seller seller) {
        this.role = "SELLER";
        this.id = seller.getSellerUid();
        this.loginId = seller.getUserId();
        this.name = seller.getCompanyName(); // Seller has company name
    }
}
