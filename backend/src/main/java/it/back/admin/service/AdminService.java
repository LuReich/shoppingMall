package it.back.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.admin.specification.BuyerSpecifications;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.admin.specification.SellerSpecifications;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
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

    public PageResponseDTO<BuyerDTO> findAllBuyers(PageRequestDTO pageRequestDTO, Long buyerUid, String buyerId, String nickname, String buyerEmail, String phone, Boolean isActive, String withdrawalStatus) {
        Pageable pageable = pageRequestDTO.toPageable();

        Specification<BuyerEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        spec = spec.and(BuyerSpecifications.hasBuyerUid(buyerUid));
        spec = spec.and(BuyerSpecifications.hasBuyerId(buyerId));
        spec = spec.and(BuyerSpecifications.hasNickname(nickname));
        spec = spec.and(BuyerSpecifications.hasBuyerEmail(buyerEmail));
        spec = spec.and(BuyerSpecifications.hasPhone(phone));
        spec = spec.and(BuyerSpecifications.isActive(isActive));
        spec = spec.and(BuyerSpecifications.hasWithdrawalStatus(withdrawalStatus));

        Page<BuyerEntity> page = buyerRepository.findAll(spec, pageable);
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
        return new PageResponseDTO<>(page, buyerList);
    }

    public PageResponseDTO<SellerDTO> findAllSellers(PageRequestDTO pageRequestDTO, Long sellerUid, String sellerId, String companyName,
            String sellerEmail, String phone, String businessRegistrationNumber, Boolean isActive, Boolean isVerified,
            String withdrawalStatus) {
        Pageable pageable = pageRequestDTO.toPageable();

        Specification<SellerEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        spec = spec.and(SellerSpecifications.hasSellerUid(sellerUid));
        spec = spec.and(SellerSpecifications.hasSellerId(sellerId));
        spec = spec.and(SellerSpecifications.hasCompanyName(companyName));
        spec = spec.and(SellerSpecifications.hasSellerEmail(sellerEmail));
        spec = spec.and(SellerSpecifications.hasPhone(phone));
        spec = spec.and(SellerSpecifications.hasBusinessRegistrationNumber(businessRegistrationNumber));
        spec = spec.and(SellerSpecifications.isActive(isActive));
        spec = spec.and(SellerSpecifications.isVerified(isVerified));
        spec = spec.and(SellerSpecifications.hasWithdrawalStatus(withdrawalStatus));

        Page<SellerEntity> page = sellerRepository.findAll(spec, pageable);
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
        return new PageResponseDTO<>(page, sellerList);
    }

}
