package it.back.seller.service;

import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import it.back.seller.dto.SellerDTO;
import it.back.seller.entity.Seller;
import it.back.seller.entity.SellerDetail;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {
    public java.util.List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public String login(LoginRequestDTO dto) {
        Seller seller = sellerRepository.findBySellerId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), seller.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return jwtUtils.createJwt(seller.getSellerId(), "SELLER", 10 * 60 * 60 * 1000L);
    }

    @Transactional
    public Seller registerSeller(SellerDTO sellerDto) {
        Seller seller = new Seller();
    seller.setSellerId(sellerDto.getSellerId());
        seller.setPassword(passwordEncoder.encode(sellerDto.getPassword())); // Hashing added
        seller.setCompanyName(sellerDto.getCompanyName());

        SellerDetail detail = new SellerDetail();
        detail.setBusinessRegistrationNumber(sellerDto.getBusinessRegistrationNumber());
        detail.setPhone(sellerDto.getPhone());
        detail.setAddress(sellerDto.getAddress());
        detail.setAddressDetail(sellerDto.getAddressDetail());

        detail.setSeller(seller);
        seller.setSellerDetail(detail);

        return sellerRepository.save(seller);
    }
}