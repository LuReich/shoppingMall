package it.back.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"sellerUid", "companyName", "sellerEmail", "businessRegistrationNumber",
    "companyInfo", "phone", "address", "addressDetail", "isVerified", "isActive", "createAt", "updateAt"})
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
}
