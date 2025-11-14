package it.back.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.admin.dto.AdminResponseDTO;
import it.back.admin.entity.AdminEntity;
import it.back.admin.dto.AdminUpdateMeRequestDTO;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.admin.specification.AdminBuyerSpecification;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.JWTUtils;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import it.back.seller.dto.SellerDTO;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.admin.specification.AdminSellerSpecification;
import it.back.admin.dto.AdminUpdateSellerRequestDTO;
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.entity.SellerDetailEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final Validator validator;
    private final ProductRepository productRepository;

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

    @Transactional
    public AdminResponseDTO updateMyInfo(String adminId, AdminUpdateMeRequestDTO dto) {
        AdminEntity admin = getAdminEntityById(adminId);

        // 이름 변경
        if (dto.getAdminName() != null && !dto.getAdminName().isBlank()) {
            admin.setAdminName(dto.getAdminName());
        }

        // 이메일 변경 (중복 체크 포함)
        if (dto.getAdminEmail() != null && !dto.getAdminEmail().isBlank()) {
            // 변경하려는 이메일이 현재 이메일과 다를 경우에만 중복 검사
            if (!admin.getAdminEmail().equals(dto.getAdminEmail())) {
                adminRepository.findByAdminEmail(dto.getAdminEmail()).ifPresent(existingAdmin -> {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                });
                admin.setAdminEmail(dto.getAdminEmail());
            }
        }

        // 비밀번호 변경
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setAdminPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // JPA의 Dirty Checking에 의해 트랜잭션 종료 시 자동으로 update 쿼리가 실행됩니다.
        // 명시적으로 save를 호출할 필요는 없지만, 명확성을 위해 호출할 수도 있습니다.
        AdminEntity updatedAdmin = adminRepository.save(admin);

        // DTO로 변환하여 반환
        return new AdminResponseDTO(updatedAdmin);
    }

    public PageResponseDTO<BuyerDTO> findAllBuyers(PageRequestDTO pageRequestDTO, Long buyerUid, String buyerId, String nickname, String buyerEmail, String phone, Boolean isActive, String withdrawalStatus) {
        Pageable pageable = pageRequestDTO.toPageable();

        Specification<BuyerEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        spec = spec.and(AdminBuyerSpecification.hasBuyerUid(buyerUid));
        spec = spec.and(AdminBuyerSpecification.hasBuyerId(buyerId));
        spec = spec.and(AdminBuyerSpecification.hasNickname(nickname));
        spec = spec.and(AdminBuyerSpecification.hasBuyerEmail(buyerEmail));
        spec = spec.and(AdminBuyerSpecification.hasPhone(phone));
        spec = spec.and(AdminBuyerSpecification.isActive(isActive));
        spec = spec.and(AdminBuyerSpecification.hasWithdrawalStatus(withdrawalStatus));

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

        spec = spec.and(AdminSellerSpecification.hasSellerUid(sellerUid));
        spec = spec.and(AdminSellerSpecification.hasSellerId(sellerId));
        spec = spec.and(AdminSellerSpecification.hasCompanyName(companyName));
        spec = spec.and(AdminSellerSpecification.hasSellerEmail(sellerEmail));
        spec = spec.and(AdminSellerSpecification.hasPhone(phone));
        spec = spec.and(AdminSellerSpecification.hasBusinessRegistrationNumber(businessRegistrationNumber));
        spec = spec.and(AdminSellerSpecification.isActive(isActive));
        spec = spec.and(AdminSellerSpecification.isVerified(isVerified));
        spec = spec.and(AdminSellerSpecification.hasWithdrawalStatus(withdrawalStatus));

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
