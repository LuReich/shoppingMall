package it.back.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public AdminEntity getAdminEntityById(String adminId) {
        return adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
    }

    public String login(LoginRequestDTO dto) {
        AdminEntity admin = adminRepository.findByAdminId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getAdminPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 10 hours token validity
        return jwtUtils.createJwt(
                admin.getAdminUid() != null ? admin.getAdminUid().longValue() : null, // uid (Long)
                admin.getAdminId(), // userId (String)
                admin.getAdminName(), // userNickname (String, 관리자명)
                "ADMIN", // userRole
                10 * 60 * 60 * 1000L // 만료(ms)
        );
    }

    /*
    // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
    // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
     */
    public PageResponseDTO<BuyerDTO> findAllBuyers(Pageable pageable) {
        Page<BuyerEntity> page = buyerRepository.findAll(pageable);
        List<BuyerDTO> buyerList = page.getContent().stream()
                .map(entity -> {
                    BuyerDTO dto = new BuyerDTO();
                    dto.setBuyerUid(entity.getBuyerUid());
                    dto.setBuyerId(entity.getBuyerId());
                    dto.setNickname(entity.getNickname());
                    dto.setBuyerEmail(entity.getBuyerEmail());
                    dto.setCreateAt(entity.getCreateAt());
                    dto.setUpdateAt(entity.getUpdateAt());
                    dto.setIsActive(entity.isActive());
                    dto.setWithdrawalStatus(entity.getWithdrawalStatus() != null ? entity.getWithdrawalStatus().name() : null);
                    dto.setWithdrawalReason(entity.getWithdrawalReason());
                    return dto;
                })
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

    public PageResponseDTO<SellerDTO> findAllSellers(Pageable pageable) {
        Page<SellerEntity> page = sellerRepository.findAll(pageable);
        List<SellerDTO> sellerList = page.getContent().stream()
                .map(entity -> {
                    SellerDTO dto = new SellerDTO();
                    dto.setSellerUid(entity.getSellerUid());
                    dto.setSellerId(entity.getSellerId());
                    dto.setSellerEmail(entity.getSellerEmail());
                    dto.setCompanyName(entity.getCompanyName());
                    dto.setIsVerified(entity.isVerified());
                    dto.setIsActive(entity.isActive());
                    dto.setWithdrawalStatus(entity.getWithdrawalStatus() != null ? entity.getWithdrawalStatus().name() : null);
                    dto.setWithdrawalReason(entity.getWithdrawalReason());
                    dto.setCreateAt(entity.getCreateAt());
                    dto.setUpdateAt(entity.getUpdateAt());
                    return dto;
                })
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
