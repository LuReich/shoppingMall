package it.back.buyer.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerRegisterDTO;
import it.back.buyer.dto.BuyerResponseDTO;
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.entity.BuyerDetailEntity;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

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

        // 비밀번호: 프론트에서 새 비밀번호가 입력된 경우만 변경, 아니면 기존 값 유지
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            buyer.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        // 닉네임 등 기타 필드
        if (req.getNickname() != null) {
            buyer.setNickname(req.getNickname());
        }
        if (req.getBuyerEmail() != null && !req.getBuyerEmail().isBlank()) {
            String email = req.getBuyerEmail();
            // 간단한 이메일 형식 검증 (정규식)
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
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
        // 변경사항은 @Transactional에 의해 자동 반영
        return buyer;
    }

    /**
     * 전화번호 중복/본인/사용가능 체크
     *
     * @param phone 전화번호
     * @param loginId 로그인한 아이디(없으면 null)
     * @return DUPLICATE(타인), SAME(본인), OK(사용가능)
     */
    public String checkPhone(String phone, String loginId) {
        if (phone == null || phone.isBlank()) {
            return "전화번호를 입력하세요.";
        }
        if (!phone.matches("\\d+")) {
            return "전화번호는 숫자만 입력해야 합니다.";
        }
        if (phone.length() < 10 || phone.length() > 11) {
            return "전화번호는 10~11자리여야 합니다.";
        }
        String result = buyerRepository.findAll().stream()
                .filter(b -> b.getBuyerDetail() != null && phone.equals(b.getBuyerDetail().getPhone()))
                .map(b -> loginId != null && loginId.equals(b.getBuyerId()) ? "SAME" : "DUPLICATE")
                .findFirst().orElse("OK");
        if ("DUPLICATE".equals(result)) {
            return "이미 사용 중인 전화번호입니다.";
        } else if ("SAME".equals(result)) {
            return "이전과 동일한 전화번호입니다.";
        }
        return "사용 가능한 전화번호입니다.";
    }

    public String checkEmail(String email, String loginId) {
        if (email == null || email.isBlank()) {
            return "이메일을 입력하세요.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "이메일 형식이 올바르지 않습니다.";
        }
        String result = buyerRepository.findByBuyerEmail(email)
                .map(b -> loginId != null && loginId.equals(b.getBuyerId()) ? "SAME" : "DUPLICATE")
                .orElse("OK");
        if ("DUPLICATE".equals(result)) {
            return "이미 사용 중인 이메일입니다.";
        } else if ("SAME".equals(result)) {
            return "이전과 동일한 이메일입니다.";
        }
        return "사용 가능한 이메일입니다.";
    }

    @Transactional
    public BuyerResponseDTO registerBuyer(BuyerRegisterDTO buyerRegisterDto) {
        // 아이디(buyer_id) 중복 체크
        if (buyerRepository.findByBuyerId(buyerRegisterDto.getBuyerId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        // 이메일 중복 체크
        if (buyerRepository.findByBuyerEmail(buyerRegisterDto.getBuyerEmail()).isPresent()) {
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
}
