package it.back.buyer.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import it.back.review.entity.ReviewEntity;
import it.back.review.dto.ReviewDTO;
import it.back.common.pagination.PageRequestDTO;
import it.back.common.pagination.PageResponseDTO;
import it.back.review.specification.ReviewSpecifications;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerRegisterDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerDetailEntity;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.review.repository.ReviewRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final Validator validator;

    public String login(LoginRequestDTO dto) {
        BuyerEntity buyer = buyerRepository.findByBuyerId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 정지 또는 탈퇴(soft delete) 계정 처리
        // soft delete 컬럼이 있다면 아래 조건에 추가 (예: buyer.isDeleted())
        if (!buyer.isActive() /* || buyer.isDeleted() */) {
            throw new IllegalArgumentException("정지 혹은 탈퇴한 계정입니다. 문의해주세요.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), buyer.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return jwtUtils.createJwt(
                buyer.getBuyerUid(), // uid (Long)
                buyer.getBuyerId(), // userId (String)
                buyer.getNickname(), // userNickname (String)
                "BUYER", // userRole
                10 * 60 * 60 * 1000L // 만료(ms)
        );
    }

    public List<BuyerDTO> getAllBuyers() {
        return buyerRepository.findAll().stream().map(entity -> {
            BuyerDTO buyerDto = new BuyerDTO();
            buyerDto.setBuyerUid(entity.getBuyerUid());
            buyerDto.setBuyerId(entity.getBuyerId());
            buyerDto.setNickname(entity.getNickname());
            buyerDto.setBuyerEmail(entity.getBuyerEmail());
            buyerDto.setCreateAt(entity.getCreateAt());
            buyerDto.setUpdateAt(entity.getUpdateAt());
            buyerDto.setIsActive(entity.isActive());
            buyerDto.setWithdrawalStatus(entity.getWithdrawalStatus() != null ? entity.getWithdrawalStatus().name() : null);
            buyerDto.setWithdrawalReason(entity.getWithdrawalReason());
            return buyerDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public BuyerEntity updateBuyer(Long buyerUid, BuyerUpdateRequestDTO req, Authentication authentication) {
        if (authentication == null) {
            throw new SecurityException("Unauthorized");
        }
        String loginId = (String) authentication.getPrincipal();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        String role = (String) details.get("role");
        BuyerEntity buyer = buyerRepository.findById(buyerUid)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        // ADMIN은 모든 buyer 수정 가능, BUYER는 본인만 가능
        if (!buyer.getBuyerId().equals(loginId) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("본인 또는 관리자만 수정할 수 있습니다.");
        }

        // PATCH: password가 null 또는 빈문자열이면 기존 비밀번호 유지, 값이 있으면 유효성 검사 후 변경
        if (req.getPassword() != null) {
            if (req.getPassword().isBlank()) {
                // 빈 문자열이면 기존 비밀번호 유지 (아무것도 하지 않음)
            } else {
                // 값이 있고, 공백 포함 등 유효성 위반이면 400 반환
                Set<ConstraintViolation<BuyerUpdateRequestDTO>> pwViolations = validator.validateProperty(req, "password");
                if (!pwViolations.isEmpty()) {
                    throw new ConstraintViolationException(pwViolations);
                }
                buyer.setPassword(passwordEncoder.encode(req.getPassword()));
            }
        }
        // 닉네임 등 기타 필드
        if (req.getNickname() != null) {
            buyer.setNickname(req.getNickname());
        }
        if (req.getBuyerEmail() != null && !req.getBuyerEmail().isBlank()) {
            String email = req.getBuyerEmail();
            if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
            }
            // DTO의 이메일 유효성 검사 (엔티티와 동일한 @Email 등 적용)
            Set<ConstraintViolation<BuyerUpdateRequestDTO>> emailViolations = validator.validateProperty(req, "buyerEmail");
            if (!emailViolations.isEmpty()) {
                throw new ConstraintViolationException(emailViolations);
            }
            // 이메일 중복 체크 (본인 제외)
            buyerRepository.findByBuyerEmail(email).ifPresent(existing -> {
                if (!existing.getBuyerUid().equals(buyerUid)) {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }
            });
            buyer.setBuyerEmail(email);
        }

        BuyerDetailEntity detail = buyer.getBuyerDetail();
        if (detail != null) {
            if (req.getPhone() != null) {
                String phone = req.getPhone();
                if (!phone.matches("\\d+")) {
                    throw new IllegalArgumentException("전화번호는 숫자만 입력해야 합니다.");
                }
                if (phone.length() < 10 || phone.length() > 11) {
                    throw new IllegalArgumentException("전화번호는 10~11자리여야 합니다.");
                }
                // 전화번호 중복 체크 (본인 제외)
                buyerRepository.findAll().stream()
                        .filter(b -> b.getBuyerDetail() != null && phone.equals(b.getBuyerDetail().getPhone()))
                        .filter(b -> !b.getBuyerUid().equals(buyerUid))
                        .findAny()
                        .ifPresent(b -> {
                            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
                        });
                detail.setPhone(phone);
            }
            if (req.getAddress() != null) {
                detail.setAddress(req.getAddress());
            }
            if (req.getAddressDetail() != null) {
                detail.setAddressDetail(req.getAddressDetail());
            }
            if (req.getBirth() != null) {
                detail.setBirth(req.getBirth());
            }
            if (req.getGender() != null) {
                try {
                    detail.setGender(BuyerDetailEntity.Gender.valueOf(req.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("gender 값이 올바르지 않습니다. (허용값: MALE, FEMALE, UNSELECTED)");
                }
            }
        }

        // Explicitly validate the entity before returning (which triggers save by @Transactional)
        Set<ConstraintViolation<BuyerEntity>> violations = validator.validate(buyer);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // 변경사항은 @Transactional에 의해 자동 반영
        return buyer;
    }

    /**
     * 전화번호 유효성 및 중복 체크.
     * @param phone 전화번호
     * @param buyerUid 중복 검사에서 제외할 buyer의 UID (본인/수정대상)
     * @return true: phone이 buyerUid의 것과 동일, false: 사용 가능한 새 번호
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 번호
     */
    public boolean checkPhone(String phone, Long buyerUid) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("전화번호를 입력하세요.");
        }
        if (!phone.matches("\\d+")) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력해야 합니다.");
        }
        if (phone.length() < 10 || phone.length() > 11) {
            throw new IllegalArgumentException("전화번호는 10~11자리여야 합니다.");
        }
        
        Optional<BuyerEntity> existing = buyerRepository.findAll().stream()
                .filter(b -> b.getBuyerDetail() != null && phone.equals(b.getBuyerDetail().getPhone()))
                .findFirst();
        
        if (existing.isEmpty()) {
            return false; // 사용 가능한 새 전화번호
        }

        BuyerEntity found = existing.get();
        if (buyerUid != null && buyerUid.equals(found.getBuyerUid())) {
            return true; // 이전과 동일한 전화번호
        }

        throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
    }

    /**
     * 이메일 유효성 및 중복 체크.
     * @param email 이메일
     * @param buyerUid 중복 검사에서 제외할 buyer의 UID (본인/수정대상)
     * @return true: email이 buyerUid의 것과 동일, false: 사용 가능한 새 이메일
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 이메일
     */
    public boolean checkEmail(String email, Long buyerUid) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력하세요.");
        }
        if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        
        Optional<BuyerEntity> existing = buyerRepository.findByBuyerEmail(email);
        if (existing.isEmpty()) {
            return false; // 사용 가능한 새 이메일
        }
        
        BuyerEntity found = existing.get();
        if (buyerUid != null && buyerUid.equals(found.getBuyerUid())) {
            return true; // 이전과 동일한 이메일 (자신 혹은 수정 대상의 이메일)
        }
        
        throw new IllegalArgumentException("이미 사용 중인 이메일입니다."); // 타인의 중복 이메일
    }

    /**
     * 아이디 유효성 및 중복 체크.
     * @param buyerId 아이디
     * @param buyerUid 중복 검사에서 제외할 buyer의 UID (본인/수정대상)
     * @return true: buyerId가 buyerUid의 것과 동일, false: 사용 가능한 새 아이디
     * @throws IllegalArgumentException 형식 오류 또는 타인의 중복 아이디
     */
    public boolean checkBuyerId(String buyerId, Long buyerUid) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
        // 아이디는 영문으로 시작해야 하며, 6~20자의 영문 또는 숫자 조합이어야 합니다.
        if (!buyerId.matches("^[a-zA-Z][a-zA-Z0-9]{5,19}$")) {
            throw new IllegalArgumentException("아이디는 영문으로 시작해야 하며, 6~20자의 영문 또는 숫자 조합이어야 합니다.");
        }

        Optional<BuyerEntity> existing = buyerRepository.findByBuyerId(buyerId);
        if (existing.isEmpty()) {
            return false; // 사용 가능한 새 아이디
        }

        BuyerEntity found = existing.get();
        if (buyerUid != null && buyerUid.equals(found.getBuyerUid())) {
            return true; // 이전과 동일한 아이디 (자신 혹은 수정 대상의 아이디)
        }

        throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); // 타인의 중복 아이디
    }

    @Transactional
    public BuyerResponseDTO registerBuyer(BuyerRegisterDTO buyerRegisterDto) {
        // 아이디(buyer_id) 중복 체크
        if (buyerRepository.findByBuyerId(buyerRegisterDto.getBuyerId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        // 이메일 중복 및 형식 체크
        String email = buyerRegisterDto.getBuyerEmail();
        if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (buyerRepository.findByBuyerEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 전화번호 중복 체크
        boolean phoneExists = buyerRepository.findAll().stream()
                .anyMatch(b -> b.getBuyerDetail() != null && buyerRegisterDto.getPhone().equals(b.getBuyerDetail().getPhone()));
        if (phoneExists) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        BuyerEntity buyer = new BuyerEntity();
        buyer.setBuyerId(buyerRegisterDto.getBuyerId());
        buyer.setPassword(passwordEncoder.encode(buyerRegisterDto.getPassword())); // Hashing added
        buyer.setNickname(buyerRegisterDto.getNickname());
        buyer.setBuyerEmail(buyerRegisterDto.getBuyerEmail());

        BuyerDetailEntity detail = new BuyerDetailEntity();
        detail.setPhone(buyerRegisterDto.getPhone());
        detail.setAddress(buyerRegisterDto.getAddress());
        detail.setAddressDetail(buyerRegisterDto.getAddressDetail());
        detail.setBirth(buyerRegisterDto.getBirth());
        detail.setGender(buyerRegisterDto.getGender());

        detail.setBuyer(buyer);
        buyer.setBuyerDetail(detail);

        // Explicitly validate the entity
        Set<ConstraintViolation<BuyerEntity>> violations = validator.validate(buyer);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        BuyerEntity saved = buyerRepository.save(buyer);
        return new BuyerResponseDTO(saved);
    }

    @Transactional
    public void buyerWithdraw(String loginId, String withdrawalReason) {
        BuyerEntity buyer = buyerRepository.findByBuyerId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 buyerId 없음: " + loginId));
        buyer.setActive(false);
        buyer.setWithdrawalStatus(BuyerEntity.WithdrawalStatus.VOLUNTARY);
        buyer.setWithdrawalReason(withdrawalReason);
        buyerRepository.save(buyer);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ReviewDTO> getMyReviews(Long buyerUid, PageRequestDTO pageRequestDTO, String productName) {
        Pageable pageable = pageRequestDTO.toPageable();

        Specification<ReviewEntity> spec = ReviewSpecifications.hasBuyerId(buyerUid)
                .and(ReviewSpecifications.productNameContains(productName));

        Page<ReviewEntity> page = reviewRepository.findAll(spec, pageable);

        List<ReviewDTO> dtos = page.getContent().stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(review.getReviewId());
            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setCreateAt(review.getCreateAt());
            dto.setUpdateAt(review.getUpdateAt());
            dto.setBuyerNickname(review.getBuyer().getNickname());
            dto.setBuyerUid(review.getBuyer().getBuyerUid());
            dto.setCompanyName(review.getProduct().getSeller().getCompanyName());
            dto.setSellerUid(review.getProduct().getSeller().getSellerUid());
            dto.setProductName(review.getProduct().getProductName());
            dto.setProductId(review.getProduct().getProductId());
            dto.setOrderDetailId(review.getOrderDetail().getOrderDetailId());
            return dto;
        }).toList();

        return new PageResponseDTO<>(page, dtos);
    }
}
