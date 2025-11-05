package it.back.faq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import it.back.admin.entity.AdminEntity;

@Entity
@Table(name = "faq")
@Getter
@Setter
public class FaqEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Integer faqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_uid")
    private AdminEntity admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "faq_target", nullable = false)
    private FaqTarget faqTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "faq_category", nullable = false)
    private FaqCategory faqCategory;

    @Column(name = "faq_question", nullable = false)
    private String faqQuestion;

    @Column(name = "faq_answer", nullable = false, columnDefinition = "TEXT")
    private String faqAnswer;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // Enum definitions for FaqTarget and FaqCategory
    public enum FaqTarget {
        BUYER, SELLER, ALL
    }

    public enum FaqCategory {
        ACCOUNT, PAYMENT, SHIPPING, PRODUCT, ETC, VERIFICATION
    }
}
