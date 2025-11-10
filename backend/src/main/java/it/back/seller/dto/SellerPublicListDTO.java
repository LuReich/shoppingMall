package it.back.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"sellerUid", "companyName", "sellerEmail", "businessRegistrationNumber",
    "companyInfo", "phone", "address", "addressDetail", "isVerified", "isActive", "createAt", "updateAt", "totalLikes", "averageRating", "totalReviews"})
@Getter
@Setter
public class SellerPublicListDTO {

    private Long sellerUid;
    private String companyName;
    private String sellerEmail;
    private String businessRegistrationNumber;
    private String companyInfo;
    private String phone;
    private String address;
    private String addressDetail;
    @JsonProperty("isVerified")
    private Boolean isVerified;
    @JsonProperty("isActive")
    private Boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;
    private Long totalLikes;
    private Double averageRating;
    private Long totalReviews;

    public SellerPublicListDTO() {
    }

    public SellerPublicListDTO(Long sellerUid, String companyName, String sellerEmail, String businessRegistrationNumber, String companyInfo, String phone, String address, String addressDetail, Boolean isVerified, Boolean isActive, LocalDateTime createAt, LocalDateTime updateAt, Long totalLikes, Double averageRating, Long totalReviews) {
        this.sellerUid = sellerUid;
        this.companyName = companyName;
        this.sellerEmail = sellerEmail;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.companyInfo = companyInfo;
        this.phone = phone;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isVerified = isVerified;
        this.isActive = isActive;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.totalLikes = totalLikes;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }
}
