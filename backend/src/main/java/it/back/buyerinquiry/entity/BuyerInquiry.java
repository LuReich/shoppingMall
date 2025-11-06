package it.back.buyerinquiry.entity;

import it.back.buyer.entity.BuyerEntity;
import it.back.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buyer_inquiry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerInquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_uid", nullable = false)
    private BuyerEntity buyer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @OneToMany(mappedBy = "buyerInquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<InquiryImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "buyerInquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private InquiryAnswer answer;
}
