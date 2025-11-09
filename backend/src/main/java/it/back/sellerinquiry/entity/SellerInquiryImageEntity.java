package it.back.sellerinquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "seller_inquiry_image")
public class SellerInquiryImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private SellerInquiryEntity sellerInquiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "uploader_type", nullable = false)
    private UploaderType uploaderType;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(name = "stored_name", nullable = false, unique = true, length = 255)
    private String storedName;

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath;

    @Column(name = "image_size")
    private Long imageSize;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    public enum UploaderType {
        USER, ADMIN
    }
}
