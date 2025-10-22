
package it.back.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.back.admin.dto.UserSummaryDTO;
import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.repository.SellerRepository;
import it.back.seller.entity.SellerEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public String login(LoginRequestDTO dto) {
    AdminEntity admin = adminRepository.findByAdminId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!passwordEncoder.matches(dto.getPassword(), admin.getAdminPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 10 hours token validity
    return jwtUtils.createJwt(admin.getAdminId(), "ADMIN", 10 * 60 * 60 * 1000L);
    }

    public List<UserSummaryDTO> findAllAdmins() {
        return adminRepository.findAll().stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    public Page<BuyerEntity> findAllBuyers(Pageable pageable) {
        return buyerRepository.findAll(pageable);
    }

    public Page<SellerEntity> findAllSellers(Pageable pageable) {
        return sellerRepository.findAll(pageable);
    }

}