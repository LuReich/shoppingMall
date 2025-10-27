package it.back.admin.dto;

import it.back.admin.entity.AdminEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminResponseDTO {
    private Integer adminUid;
    private String adminId;
    private String adminName;
    private String adminEmail;
    private Byte permissionLevel;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String role;

    public AdminResponseDTO(AdminEntity admin) {
        this.adminUid = admin.getAdminUid();
        this.adminId = admin.getAdminId();
        this.adminName = admin.getAdminName();
        this.adminEmail = admin.getAdminEmail();
        this.permissionLevel = admin.getPermissionLevel();
        this.createAt = admin.getCreateAt();
        this.updateAt = admin.getUpdateAt();
        this.role = "ADMIN";
    }
}
