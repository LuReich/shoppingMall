package it.back.seller.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seller")
@Getter
@Setter
public class SellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_uid")
    private Long sellerUid;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "아이디에는 공백을 포함할 수 없습니다.")
    @Column(name = "seller_id", unique = true, nullable = false, length = 50)
    private String sellerId;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "이메일에는 공백을 포함할 수 없습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "seller_email", unique = true, nullable = false, length = 100)
    private String sellerEmail;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\S*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_status")
    private WithdrawalStatus withdrawalStatus;

    @Column(name = "withdrawal_reason", columnDefinition = "TEXT")
    private String withdrawalReason;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SellerDetailEntity sellerDetail;

    public enum WithdrawalStatus {
        VOLUNTARY, FORCED_BY_ADMIN
    }
}
