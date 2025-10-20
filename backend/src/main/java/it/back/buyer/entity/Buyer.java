package it.back.buyer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "buyer")
@Getter
@Setter
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_uid")
    private Long buyerUid;

    @Column(name = "buyer_id", unique = true, nullable = false, length = 50)
    private String buyerId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_status")
    private WithdrawalStatus withdrawalStatus;

    @Column(name = "withdrawal_reason", columnDefinition = "TEXT")
    private String withdrawalReason;

    @OneToOne(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BuyerDetail buyerDetail;

    public enum WithdrawalStatus {
        VOLUNTARY, FORCED_BY_ADMIN
    }
}
