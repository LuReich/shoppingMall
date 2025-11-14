package it.back.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.back.admin.entity.AdminEntity;

public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    Optional<AdminEntity> findByAdminId(String adminId);

    Optional<AdminEntity> findByAdminEmail(String adminEmail);
}
