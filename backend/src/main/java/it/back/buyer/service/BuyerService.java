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
import it.back.buyer.dto.BuyerUpdateRequestDTO;
import it.back.buyer.dto.BuyerRegisterDTO;
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
            if (entity.getBuyerDetail() != null) {
                buyerDto.setPhone(entity.getBuyerDetail().getPhone());
                buyerDto.setAddress(entity.getBuyerDetail().getAddress());
                buyerDto.setAddressDetail(entity.getBuyerDetail().getAddressDetail());
                buyerDto.setBirth(entity.getBuyerDetail().getBirth());
                buyerDto.setGender(entity.getBuyerDetail().getGender());
            }
            return buyerDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateBuyer(Long buyerUid, BuyerUpdateRequestDTO req, Authentication authentication) {
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
            buyer.setBuyerEmail(req.getBuyerEmail());
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
                    // 잘못된 값이 오면 무시(혹은 예외 처리)
                }
            }
        }
        // 변경사항은 @Transactional에 의해 자동 반영
    }

    @Transactional
    public BuyerEntity registerBuyer(BuyerRegisterDTO buyerRegisterDto) {
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

        return buyerRepository.save(buyer);
    }
}
