package it.back.buyer.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "buyer_detail")
@Getter
@Setter
public class BuyerDetailEntity {

    @Id
    @Column(name = "buyer_uid")
    private Long buyerUid;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "buyer_uid")
    private BuyerEntity buyer;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "전화번호에는 공백을 포함할 수 없습니다.")
    @Column(name = "phone", unique = true, nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public enum Gender {
        MALE, FEMALE, UNSELECTED
    }
}
