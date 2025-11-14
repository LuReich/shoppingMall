
package it.back.seller.service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.admin.dto.AdminUpdateSellerRequestDTO;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.dto.SellerPublicDTO;
import it.back.seller.dto.SellerPublicListDTO;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import it.back.seller.dto.SellerRegisterDTO; // New import
import it.back.seller.dto.SellerResponseDTO;
import it.back.seller.dto.SellerUpdateRequestDTO;
import it.back.seller.entity.SellerDetailEntity;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.seller.specification.SellerPublicSpecification;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final Validator validator;
    private final ProductRepository productRepository;


    public String login(LoginRequestDTO dto) {
        SellerEntity seller = sellerRepository.findBySellerId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 정지 또는 탈퇴(soft delete) 계정 처리
        // soft delete 컬럼이 있다면 아래 조건에 추가 (예: seller.isDeleted())
        if (!seller.isActive() /* || seller.isDeleted() */) {
            throw new IllegalArgumentException("정지 혹은 탈퇴한 계정입니다. 문의해주세요.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), seller.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return jwtUtils.createJwt(
                seller.getSellerUid(), // uid (Long)
                seller.getSellerId(), // userId (String)
                seller.getCompanyName(), // userNickname (String, 회사명)
                "SELLER", // userRole
                10 * 60 * 60 * 1000L // 만료(ms)
        );
    }

    // (중복 import, 클래스 선언, 필드 선언 제거)
    public List<SellerDTO> getAllSellers(Sort sort) {
        return sellerRepository.findAll(sort).stream().map(entity -> {
            SellerDTO sellerDto = new SellerDTO();
            sellerDto.setSellerUid(entity.getSellerUid());
            sellerDto.setSellerId(entity.getSellerId());
            sellerDto.setSellerEmail(entity.getSellerEmail());
            sellerDto.setCompanyName(entity.getCompanyName());
            sellerDto.setIsVerified(entity.isVerified());
            sellerDto.setIsActive(entity.isActive());
            sellerDto.setWithdrawalStatus(entity.getWithdrawalStatus() != null ? entity.getWithdrawalStatus().name() : null);
            sellerDto.setWithdrawalReason(entity.getWithdrawalReason());
            sellerDto.setCreateAt(entity.getCreateAt());
            sellerDto.setUpdateAt(entity.getUpdateAt());

            return sellerDto;
        }).collect(Collectors.toList());
    }

    // 공개용 판매자 정보 조회
    public SellerPublicDTO getSellerPublicInfo(Long sellerUid) {
        return sellerRepository.findSellerPublicInfoById(sellerUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 판매자 없음: " + sellerUid));
    }

    public PageResponseDTO<SellerPublicListDTO> getSellerPublicList(PageRequestDTO pageRequestDTO, Long sellerUid, String companyName, String businessRegistrationNumber, String phone, String address, Boolean isVerified) {
        Page<SellerPublicListDTO> sellers = sellerRepository.findSellerPublicList(pageRequestDTO, sellerUid, companyName, businessRegistrationNumber, phone, address, isVerified);
        return new PageResponseDTO<>(sellers);
    }

    private SellerPublicListDTO convertToSellerPublicListDTO(SellerEntity seller) {
        SellerPublicListDTO dto = new SellerPublicListDTO();
        dto.setSellerUid(seller.getSellerUid());
        dto.setCompanyName(seller.getCompanyName());
        dto.setSellerEmail(seller.getSellerEmail());
        dto.setIsVerified(seller.isVerified());
        dto.setIsActive(seller.isActive());
        dto.setCreateAt(seller.getCreateAt());
        dto.setUpdateAt(seller.getUpdateAt());

        SellerDetailEntity detail = seller.getSellerDetail();
        if (detail != null) {
            dto.setBusinessRegistrationNumber(detail.getBusinessRegistrationNumber());
            dto.setCompanyInfo(detail.getCompanyInfo());
            dto.setPhone(detail.getPhone());
            dto.setAddress(detail.getAddress());
            dto.setAddressDetail(detail.getAddressDetail());
        }
        return dto;
    }

    /*
     * // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
     * // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
     */
    @Transactional
    public SellerResponseDTO registerSeller(SellerRegisterDTO sellerRegisterDto) {
        // 이메일 형식 체크 (영문, 숫자, . _ - 만 허용, 한글/특수문자 불가, @ 오른쪽은 도메인 형식만 허용)
        String email = sellerRegisterDto.getSellerEmail();
        if (email == null || !email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        // 전화번호 숫자열(10~11자리)만 허용
        String phone = sellerRegisterDto.getPhone();
        if (phone == null || !phone.matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("전화번호는 10~11자리 숫자만 입력해야 합니다.");
        }
        // 사업자등록번호 10자리 숫자 체크
        String businessNo = sellerRegisterDto.getBusinessRegistrationNumber();
        if (businessNo == null || !businessNo.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("사업자등록번호는 10자리 숫자만 입력해야 합니다.");
        }

        // 비밀번호 유효성 검사
        Set<ConstraintViolation<SellerRegisterDTO>> pwViolations = validator.validateProperty(sellerRegisterDto, "password");
        if (!pwViolations.isEmpty()) {
            throw new ConstraintViolationException(pwViolations);
        }

        // Uniqueness checks
        if (sellerRepository.findBySellerId(sellerRegisterDto.getSellerId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (sellerRepository.findBySellerEmail(sellerRegisterDto.getSellerEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (sellerRepository.findBySellerDetail_BusinessRegistrationNumber(sellerRegisterDto.getBusinessRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사업자등록번호입니다.");
        }

        SellerEntity seller = new SellerEntity();
        seller.setSellerId(sellerRegisterDto.getSellerId());
        // 비밀번호 유효성 검사 통과 후 암호화
        seller.setPassword(passwordEncoder.encode(sellerRegisterDto.getPassword()));
        seller.setCompanyName(sellerRegisterDto.getCompanyName());
        seller.setSellerEmail(sellerRegisterDto.getSellerEmail());

        SellerDetailEntity detail = new SellerDetailEntity();
        detail.setBusinessRegistrationNumber(sellerRegisterDto.getBusinessRegistrationNumber());
        detail.setCompanyInfo(sellerRegisterDto.getCompanyInfo()); // 업체 상세 소개 저장
        detail.setPhone(sellerRegisterDto.getPhone());
        detail.setAddress(sellerRegisterDto.getAddress());
        detail.setAddressDetail(sellerRegisterDto.getAddressDetail());

        detail.setSeller(seller);
        seller.setSellerDetail(detail);

        // Explicitly validate the entity
        Set<ConstraintViolation<SellerEntity>> violations = validator.validate(seller);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        SellerEntity saved = sellerRepository.save(seller);
        return new SellerResponseDTO(saved);
    }


    /**
     * 이메일 유효성 및 중복 체크.
     * @param email 이메일
     * @param sellerUid 중복 검사에서 제외할 seller의 UID (본인/수정대상)
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 이메일
     */
    public boolean checkEmail(String email, Long sellerUid) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력하세요.");
        }
        if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        
        Optional<SellerEntity> existing = sellerRepository.findBySellerEmail(email);
        if (existing.isEmpty()) {
            return false; // Available
        }

        // If existing, check if it's the same user
        return sellerUid != null && sellerUid.equals(existing.get().getSellerUid());
    }

    /**
     * 아이디 유효성 및 중복 체크.
     * @param sellerId 아이디
     * @param sellerUid 중복 검사에서 제외할 seller의 UID (본인/수정대상)
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 아이디
     */
    public boolean checkSellerId(String sellerId, Long sellerUid) {
        if (sellerId == null || sellerId.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
        // 아이디는 영문으로 시작해야 하며, 5~20자의 영문 또는 숫자 조합이어야 합니다.
        if (!sellerId.matches("^[a-zA-Z][a-zA-Z0-9]{4,19}$")) {
            throw new IllegalArgumentException("아이디는 영문으로 시작해야 하며, 5~20자의 영문 또는 숫자 조합이어야 합니다.");
        }

        Optional<SellerEntity> existing = sellerRepository.findBySellerId(sellerId);
        if (existing.isPresent()) {
            SellerEntity found = existing.get();
            // ID가 존재하고, 그 ID가 현재 사용자의 ID와 동일한지 확인
            if (sellerUid != null && sellerUid.equals(found.getSellerUid())) {
                return true; // 현재 사용자의 아이디와 동일함
            } else {
                // 다른 사용자가 이미 사용 중인 아이디
                throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
            }
        }

        return false; // 사용 가능한 새 아이디
    }

    /**
     * 전화번호 유효성 체크. (중복 체크 없음)
     * @param phone 전화번호
     * @throws IllegalArgumentException 형식 오류 또는 길이 오류
     */
    public void checkPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("전화번호를 입력하세요.");
        }
        if (!phone.matches("^\\d+$")) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력해야 합니다.");
        }
        if (phone.length() < 10 || phone.length() > 11) {
            throw new IllegalArgumentException("전화번호는 10~11자리여야 합니다.");
        }
    }

    /**
     * 사업자등록번호 유효성 및 중복 체크.
     * @param businessRegistrationNumber 사업자등록번호
     * @param sellerUid 중복 검사에서 제외할 seller의 UID (본인/수정대상)
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 사업자등록번호
     */
    public boolean checkBusinessRegistrationNumber(String businessRegistrationNumber, Long sellerUid) {
        if (businessRegistrationNumber == null || businessRegistrationNumber.isBlank()) {
            throw new IllegalArgumentException("사업자등록번호를 입력하세요.");
        }
        if (!businessRegistrationNumber.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("사업자등록번호는 10자리 숫자만 입력해야 합니다.");
        }
        
        Optional<SellerEntity> existing = sellerRepository.findBySellerDetail_BusinessRegistrationNumber(businessRegistrationNumber);
        if (existing.isEmpty()) {
            return false; // Available
        }

        // If existing, check if it's the same user
        return sellerUid != null && sellerUid.equals(existing.get().getSellerUid());
    }

    @Transactional
    public SellerResponseDTO updateSeller(Long sellerUid, SellerUpdateRequestDTO req, Authentication authentication) {
        if (authentication == null) {
            throw new SecurityException("Unauthorized");
        }
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        String role = (String) details.get("role");
        SellerEntity seller = sellerRepository.findById(sellerUid)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        // ADMIN은 모든 seller 수정 가능, SELLER는 본인만 가능
        if (!seller.getSellerId().equals(loginId) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("본인 또는 관리자만 수정할 수 있습니다.");
        }

        // PATCH: password가 null 또는 빈문자열이면 기존 비밀번호 유지, 값이 있으면 유효성 검사 후 변경
        if (req.getPassword() != null) {
            if (req.getPassword().isBlank()) {
                // 빈 문자열이면 기존 비밀번호 유지 (아무것도 하지 않음)
            } else {
                // 값이 있고, 공백 포함 등 유효성 위반이면 400 반환
                Set<ConstraintViolation<SellerUpdateRequestDTO>> pwViolations = validator.validateProperty(req, "password");
                if (!pwViolations.isEmpty()) {
                    throw new ConstraintViolationException(pwViolations);
                }
                seller.setPassword(passwordEncoder.encode(req.getPassword()));
            }
        }
        // 기타 정보 변경(아이디는 수정 불가)
        if (req.getCompanyName() != null) {
            seller.setCompanyName(req.getCompanyName());
        }
        if (req.getSellerEmail() != null && !req.getSellerEmail().isBlank()) {
            String email = req.getSellerEmail();
            if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
            }
            // DTO의 이메일 유효성 검사 (엔티티와 동일한 @Email 등 적용)
            Set<ConstraintViolation<SellerUpdateRequestDTO>> emailViolations = validator.validateProperty(req, "sellerEmail");
            if (!emailViolations.isEmpty()) {
                throw new ConstraintViolationException(emailViolations);
            }
            // 이메일 중복 체크 (본인 제외)
            sellerRepository.findBySellerEmail(email).ifPresent(existing -> {
                if (!existing.getSellerUid().equals(sellerUid)) {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }
            });
            seller.setSellerEmail(email);
        }

        SellerDetailEntity detail = seller.getSellerDetail();
        if (detail != null) {
            if (req.getBusinessRegistrationNumber() != null && !req.getBusinessRegistrationNumber().isBlank()) {
                // 사업자등록번호 10자리 숫자 체크
                String businessNo = req.getBusinessRegistrationNumber();
                if (!businessNo.matches("^\\d{10}$")) {
                    throw new IllegalArgumentException("사업자등록번호는 10자리 숫자만 입력해야 합니다.");
                }
                // 사업자등록번호 중복 체크 (본인 제외)
                sellerRepository.findBySellerDetail_BusinessRegistrationNumber(businessNo).ifPresent(existing -> {
                    if (!existing.getSellerUid().equals(sellerUid)) {
                        throw new IllegalArgumentException("이미 사용 중인 사업자등록번호입니다.");
                    }
                });
                detail.setBusinessRegistrationNumber(businessNo);
            }
            if (req.getPhone() != null && !req.getPhone().isBlank()) {
                // 전화번호 숫자열(10~11자리)만 허용
                String phone = req.getPhone();
                if (!phone.matches("^\\d{10,11}$")) {
                    throw new IllegalArgumentException("전화번호는 10~11자리 숫자만 입력해야 합니다.");
                }
                detail.setPhone(phone);
            }
            if (req.getAddress() != null) {
                detail.setAddress(req.getAddress());
            }
            if (req.getAddressDetail() != null) {
                detail.setAddressDetail(req.getAddressDetail());
            }
            if (req.getCompanyInfo() != null) {
                detail.setCompanyInfo(req.getCompanyInfo());
            }
        }
        // 유효성 검사
        Set<ConstraintViolation<SellerEntity>> violations = validator.validate(seller);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        // 변경사항은 @Transactional에 의해 자동 반영
        return new SellerResponseDTO(seller);
    }

    @Transactional
    public SellerResponseDTO adminUpdateSeller(Long sellerUid, AdminUpdateSellerRequestDTO dto) {
        SellerEntity seller = sellerRepository.findById(sellerUid)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with uid: " + sellerUid));

        boolean previousIsActive = seller.isActive();

        // Update sellerId
        if (dto.getSellerId() != null && !dto.getSellerId().isBlank()) {
            if (!seller.getSellerId().equals(dto.getSellerId())) { // Only check uniqueness if ID is actually changed
                checkSellerId(dto.getSellerId(), sellerUid); // Use helper for validation and uniqueness
            }
            seller.setSellerId(dto.getSellerId());
        }

        // Update password
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            Set<ConstraintViolation<AdminUpdateSellerRequestDTO>> pwViolations = validator.validateProperty(dto, "password");
            if (!pwViolations.isEmpty()) {
                throw new ConstraintViolationException(pwViolations);
            }
            seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Update companyName
        if (dto.getCompanyName() != null) {
            seller.setCompanyName(dto.getCompanyName());
        }

        // Update sellerEmail
        if (dto.getSellerEmail() != null && !dto.getSellerEmail().isBlank()) {
            if (!seller.getSellerEmail().equals(dto.getSellerEmail())) { // Only check uniqueness if email is actually changed
                checkEmail(dto.getSellerEmail(), sellerUid); // Use helper for validation and uniqueness
            }
            seller.setSellerEmail(dto.getSellerEmail());
        }

        // Update isActive
        if (dto.getIsActive() != null) {
            seller.setActive(dto.getIsActive());
        }

        // Update isVerified
        if (dto.getIsVerified() != null) {
            seller.setVerified(dto.getIsVerified());
        }

        // Update withdrawalStatus
        seller.setWithdrawalStatus(dto.getWithdrawalStatus());

        // Update withdrawalReason
        seller.setWithdrawalReason(dto.getWithdrawalReason());

        // === Product status update logic based on isActive change ===
        if (dto.getIsActive() != null && previousIsActive != dto.getIsActive()) {
            List<ProductEntity> products = productRepository.findAllBySellerSellerUid(sellerUid);
            if (dto.getIsActive()) { // Status changed to Active (false -> true)
                seller.setWithdrawalStatus(null); // Clear withdrawal status on restoration
                for (ProductEntity product : products) {
                    product.setIsDeleted(false);
                    product.setDeletedByAdminReason(null);
                    product.setDeletedBySellerReason(null);
                }
            } else { // Status changed to Inactive (true -> false)
                seller.setWithdrawalStatus(SellerEntity.WithdrawalStatus.FORCED_BY_ADMIN);
                for (ProductEntity product : products) {
                    product.setIsDeleted(true);
                }
            }
        }

        // Update SellerDetailEntity fields
        SellerDetailEntity detail = seller.getSellerDetail();
        if (detail == null) {
            detail = new SellerDetailEntity();
            detail.setSeller(seller);
            seller.setSellerDetail(detail);
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            if (!detail.getPhone().equals(dto.getPhone())) { // Only check uniqueness if phone is actually changed
                checkPhone(dto.getPhone()); // Use helper for validation and uniqueness (no sellerUid needed for non-unique check)
            }
            detail.setPhone(dto.getPhone());
        }
        if (dto.getBusinessRegistrationNumber() != null && !dto.getBusinessRegistrationNumber().isBlank()) {
            if (!detail.getBusinessRegistrationNumber().equals(dto.getBusinessRegistrationNumber())) { // Only check uniqueness if BRN is actually changed
                checkBusinessRegistrationNumber(dto.getBusinessRegistrationNumber(), sellerUid); // Use helper for validation and uniqueness
            }
            detail.setBusinessRegistrationNumber(dto.getBusinessRegistrationNumber());
        }
        if (dto.getCompanyInfo() != null) {
            detail.setCompanyInfo(dto.getCompanyInfo());
        }
        if (dto.getAddress() != null) {
            detail.setAddress(dto.getAddress());
        }
        if (dto.getAddressDetail() != null) {
            detail.setAddressDetail(dto.getAddressDetail());
        }

        // Explicitly validate the entity
        Set<ConstraintViolation<SellerEntity>> violations = validator.validate(seller);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        sellerRepository.save(seller); // Save changes
        return new SellerResponseDTO(seller);
    }

    @Transactional
    public void sellerWithdraw(String loginId, String withdrawalReason) {
        SellerEntity seller = sellerRepository.findBySellerId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 sellerId 없음: " + loginId));
        seller.setActive(false);
        seller.setWithdrawalStatus(SellerEntity.WithdrawalStatus.VOLUNTARY);
        seller.setWithdrawalReason(withdrawalReason);
        sellerRepository.save(seller);

        List<ProductEntity> products = productRepository.findAllBySellerSellerUid(seller.getSellerUid());
        for (ProductEntity product : products) {
            product.setIsDeleted(true);
            product.setDeletedBySellerReason("자진 탈퇴로 인한 상품 삭제");
        }
    }
}
