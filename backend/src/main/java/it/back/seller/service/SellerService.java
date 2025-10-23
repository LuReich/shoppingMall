package it.back.seller.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.entity.SellerDetailEntity;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public List<SellerDTO> getAllSellers() {
        return sellerRepository.findAll().stream().map(entity -> {
            SellerDTO dto = new SellerDTO();
            dto.setSellerId(entity.getSellerId());
            dto.setCompanyName(entity.getCompanyName());
            // 필요한 필드 추가
            return dto;
        }).collect(Collectors.toList());
    }

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

        return jwtUtils.createJwt(seller.getSellerId(), "SELLER", 10 * 60 * 60 * 1000L);
    }

    /*
     * // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
     * // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
     */
    @Transactional
    public SellerEntity registerSeller(SellerDTO sellerDTO) {
        SellerEntity seller = new SellerEntity();
        seller.setSellerId(sellerDTO.getSellerId());
        seller.setPassword(passwordEncoder.encode(sellerDTO.getPassword())); // Hashing added
        seller.setCompanyName(sellerDTO.getCompanyName());

        SellerDetailEntity detail = new SellerDetailEntity();
        detail.setBusinessRegistrationNumber(sellerDTO.getBusinessRegistrationNumber());
        detail.setCompanyInfo(sellerDTO.getCompanyInfo()); // 업체 상세 소개 저장
        detail.setPhone(sellerDTO.getPhone());
        detail.setAddress(sellerDTO.getAddress());
        detail.setAddressDetail(sellerDTO.getAddressDetail());
        

        detail.setSeller(seller);
        seller.setSellerDetail(detail);

        return sellerRepository.save(seller);
    }
}
