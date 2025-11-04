package it.back.seller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.back.seller.entity.SellerEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({
    "sellerUid",
    "sellerId",
    "companyName",
    "sellerEmail",
    "businessRegistrationNumber",
    "companyInfo",
    "phone",
    "address",
    "addressDetail",
    "isVerified",
    "isActive",
    "withdrawalStatus",
    "withdrawalReason",
    "createAt",
    "updateAt",
    "role"
})
public class SellerResponseDTO {


    private Long sellerUid;

    private String sellerId;
    private String companyName;
    private String sellerEmail;

    // SellerDetailEntity 정보
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;

    @JsonProperty("isVerified")
    private Boolean isVerified;
    @JsonProperty("isActive")
    private Boolean isActive;
    private String withdrawalStatus;
    private String withdrawalReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String role;

    public SellerResponseDTO(SellerEntity seller) {
        this.sellerUid = seller.getSellerUid();
        this.sellerId = seller.getSellerId();
        this.companyName = seller.getCompanyName();
        this.sellerEmail = seller.getSellerEmail();
        this.isVerified = seller.isVerified();
        this.isActive = seller.isActive();
        this.withdrawalStatus = seller.getWithdrawalStatus() != null ? seller.getWithdrawalStatus().name() : null;
        this.withdrawalReason = seller.getWithdrawalReason();
        this.createAt = seller.getCreateAt();
        this.updateAt = seller.getUpdateAt();

        if (seller.getSellerDetail() != null) {
            this.businessRegistrationNumber = seller.getSellerDetail().getBusinessRegistrationNumber();
            this.companyInfo = seller.getSellerDetail().getCompanyInfo();
            this.phone = seller.getSellerDetail().getPhone();
            this.address = seller.getSellerDetail().getAddress();
            this.addressDetail = seller.getSellerDetail().getAddressDetail();
        }
        this.role = "SELLER";
    }
}
