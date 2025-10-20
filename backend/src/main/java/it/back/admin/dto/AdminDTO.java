package it.back.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {
    private String adminId;
    private String adminPassword;
    private String adminName;
    private Byte permissionLevel;
}
