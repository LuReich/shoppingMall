package it.back.admin.service;

import java.util.List;
import java.util.stream.Collectors;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.seller.dto.SellerResponseDTO;

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

    /*
    // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
    // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
     */
    public List<UserSummaryDTO> findAllAdmins() {
        return adminRepository.findAll().stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    public PageResponseDTO<BuyerResponseDTO> findAllBuyers(Pageable pageable) {
        Page<BuyerEntity> page = buyerRepository.findAll(pageable);
        List<BuyerResponseDTO> buyerList = page.getContent().stream()
                .map(BuyerResponseDTO::new)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(
                buyerList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public PageResponseDTO<SellerResponseDTO> findAllSellers(Pageable pageable) {
        Page<SellerEntity> page = sellerRepository.findAll(pageable);
        List<SellerResponseDTO> sellerList = page.getContent().stream()
                .map(SellerResponseDTO::new)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(
                sellerList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
