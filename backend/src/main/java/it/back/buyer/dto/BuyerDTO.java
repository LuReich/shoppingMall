package it.back.buyer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerDTO {

    private Long buyerUid;
    private String buyerId;
    private String nickname;
    private String buyerEmail;
    private java.time.LocalDateTime createAt;
    private java.time.LocalDateTime updateAt;
    private Boolean isActive;
    private String withdrawalStatus;
    private String withdrawalReason;
}
