package it.back.admin.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin")
@Getter
@Setter
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_uid")
    private Integer adminUid;

    @Column(name = "admin_id", unique = true, nullable = false, length = 50)
    private String adminId;

    @Column(name = "admin_password", nullable = false, length = 255)
    private String adminPassword;

    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    @Column(name = "admin_email", unique = true, nullable = false, length = 100)
    private String adminEmail;

    @Column(name = "permission_level", nullable = false)
    private Byte permissionLevel;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
