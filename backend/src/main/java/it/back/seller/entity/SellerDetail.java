package it.back.seller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seller_detail")
@Getter
@Setter
public class SellerDetail {

    @Id
    @Column(name = "seller_uid")
    private Long sellerUid;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "seller_uid")
    private Seller seller;

    @Column(name = "business_registration_number", unique = true, nullable = false, length = 20)
    private String businessRegistrationNumber;

    @Column(name = "company_info", columnDefinition = "TEXT")
    private String companyInfo;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;
}
