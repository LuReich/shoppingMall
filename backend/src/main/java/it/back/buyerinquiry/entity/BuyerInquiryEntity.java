package it.back.buyerinquiry.entity;

import it.back.admin.entity.AdminEntity;
import it.back.buyer.entity.BuyerEntity;
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
@Table(name = "buyer_inquiry")
public class BuyerInquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_uid")
    private BuyerEntity buyer;

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

    @OneToMany(mappedBy = "buyerInquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyerInquiryImageEntity> images = new ArrayList<>();

    public enum InquiryType {
        ACCOUNT, PAYMENT, SHIPPING, ETC
    }

    public enum InquiryStatus {
        PENDING, ANSWERED
    }
}
