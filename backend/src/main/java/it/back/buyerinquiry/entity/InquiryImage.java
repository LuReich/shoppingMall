package it.back.buyerinquiry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inquiry_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private BuyerInquiry buyerInquiry;

    @Column(nullable = false)
    private String imagePath;
}
