package it.back.seller.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seller_detail")
@Getter
@Setter
public class SellerDetailEntity {

    @Id
    @Column(name = "seller_uid")
    private Long sellerUid;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "seller_uid")
    private SellerEntity seller;

    @Column(name = "business_registration_number", unique = true, nullable = false, length = 20)
    private String businessRegistrationNumber;

    @Column(name = "company_info", columnDefinition = "TEXT")
    private String companyInfo;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "전화번호에는 공백을 포함할 수 없습니다.")
    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;
}
