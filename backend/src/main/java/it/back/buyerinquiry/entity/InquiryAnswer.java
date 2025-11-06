package it.back.buyerinquiry.entity;

import it.back.admin.entity.AdminEntity;
import it.back.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inquiry_answer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private BuyerInquiry buyerInquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_uid", nullable = false)
    private AdminEntity admin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
