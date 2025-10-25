package it.back.buyer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BuyerDTO {

    private Long buyerUid;
    private String buyerId;
    private String nickname;
    private String buyerEmail;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonProperty("isActive")
    private Boolean isActive;
    private String withdrawalStatus;
    private String withdrawalReason;

}
