package it.back.seller.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.dto.SellerPublicDTO;
import it.back.seller.dto.SellerRegisterDTO;
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

    /*
     * // [form-data 방식으로 바꿀 때 서비스는 동일하게 사용 가능]
     * // 컨트롤러에서 DTO로 변환해서 넘기면 서비스 코드는 그대로 사용하면 됩니다.
     */
    @Transactional
    public SellerEntity registerSeller(SellerRegisterDTO sellerRegisterDto) {
        SellerEntity seller = new SellerEntity();
        seller.setSellerId(sellerRegisterDto.getSellerId());
        seller.setPassword(passwordEncoder.encode(sellerRegisterDto.getPassword())); // Hashing added
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

        return sellerRepository.save(seller);
    }

    // 공개용 판매자 정보 조회
    public SellerPublicDTO getSellerPublicInfo(Long sellerUid) {
        SellerEntity seller = sellerRepository.findById(sellerUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 판매자 없음: " + sellerUid));
        SellerDetailEntity detail = seller.getSellerDetail();
        SellerPublicDTO dto = new SellerPublicDTO();
        dto.setSellerUid(seller.getSellerUid());
        dto.setCompanyName(seller.getCompanyName());
        dto.setSellerEmail(seller.getSellerEmail());
        dto.setCreateAt(seller.getCreateAt());
        dto.setVerified(seller.isVerified());
        dto.setActive(seller.isActive());
        if (detail != null) {
            dto.setBusinessRegistrationNumber(detail.getBusinessRegistrationNumber());
            dto.setCompanyInfo(detail.getCompanyInfo());
            dto.setPhone(detail.getPhone());
            dto.setAddress(detail.getAddress());
            dto.setAddressDetail(detail.getAddressDetail());
        }
        return dto;
    }
}
