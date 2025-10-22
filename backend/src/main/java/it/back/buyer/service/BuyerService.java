package it.back.buyer.service;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.dto.BuyerUpdateRequest;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.entity.BuyerDetailEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerService {

    public java.util.List<BuyerEntity> getAllBuyers() {
        return buyerRepository.findAll();
    }

    @Transactional
    public void updateBuyer(Long buyerUid, BuyerUpdateRequest req, String loginId, String role) {
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
            if (req.getAddress() != null)
                detail.setAddress(req.getAddress());
            if (req.getAddressDetail() != null)
                detail.setAddressDetail(req.getAddressDetail());
            if (req.getBirth() != null)
                detail.setBirth(req.getBirth());
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

        return jwtUtils.createJwt(buyer.getBuyerId(), "BUYER", 10 * 60 * 60 * 1000L);
    }

    /*
    // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
    // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
    */

    @Transactional
    public BuyerEntity registerBuyer(BuyerDTO buyerDTO) {
        BuyerEntity buyer = new BuyerEntity();
        buyer.setBuyerId(buyerDTO.getBuyerId());
        buyer.setPassword(passwordEncoder.encode(buyerDTO.getPassword())); // Hashing added
        buyer.setNickname(buyerDTO.getNickname());

        BuyerDetailEntity detail = new BuyerDetailEntity();
        detail.setPhone(buyerDTO.getPhone());
        detail.setAddress(buyerDTO.getAddress());
        detail.setAddressDetail(buyerDTO.getAddressDetail());

        detail.setBuyer(buyer);
        buyer.setBuyerDetail(detail);

        return buyerRepository.save(buyer);
    }
}