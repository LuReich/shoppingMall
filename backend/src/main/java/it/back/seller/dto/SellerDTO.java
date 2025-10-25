package it.back.seller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class SellerDTO {

    private Long sellerUid;
    private String sellerId;
    private String sellerEmail;
    private String companyName;
    @JsonProperty("isVerified")
    private Boolean isVerified;
    @JsonProperty("isActive")
    private Boolean isActive;
    private String withdrawalStatus;
    private String withdrawalReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
