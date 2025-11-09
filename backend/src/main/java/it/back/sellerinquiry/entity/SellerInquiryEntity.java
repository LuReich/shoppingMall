package it.back.sellerinquiry.entity;

import it.back.admin.entity.AdminEntity;
import it.back.seller.entity.SellerEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "seller_inquiry")
public class SellerInquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_uid")
    private SellerEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_uid")
    private AdminEntity admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", nullable = false)
    private InquiryType inquiryType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "question_content", nullable = false, columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String answerContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    private InquiryStatus inquiryStatus = InquiryStatus.PENDING;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "answer_at")
    private LocalDateTime answerAt;

    @OneToMany(mappedBy = "sellerInquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellerInquiryImageEntity> images = new ArrayList<>();

    public enum InquiryType {
        ACCOUNT, PRODUCT, VERIFICATION, ETC
    }

    public enum InquiryStatus {
        PENDING, ANSWERED
    }
}
