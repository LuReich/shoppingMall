package it.back.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"sellerUid", "companyName", "sellerEmail", "businessRegistrationNumber",
        "companyInfo", "phone", "address", "addressDetail", "isVerified", "isActive", "createAt", "updateAt", "totalLikes", "averageRating", "totalReviews"})
public class SellerPublicDTO {

    private Long sellerUid;
    private String companyName;
    private String sellerEmail;
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;
    private Boolean isVerified;
    private Boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;
    private Long totalLikes;
    private Double averageRating;
    private Long totalReviews;

    public void setVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
