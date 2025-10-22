package it.back.buyer.service;

import it.back.buyer.dto.BuyerDTO;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.entity.BuyerDetailEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.dto.LoginRequestDTO;
import it.back.common.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerService {
    public java.util.List<BuyerEntity> getAllBuyers() {
        return buyerRepository.findAll();
    }

    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public String login(LoginRequestDTO dto) {
    BuyerEntity buyer = buyerRepository.findByBuyerId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!passwordEncoder.matches(dto.getPassword(), buyer.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

    return jwtUtils.createJwt(buyer.getBuyerId(), "BUYER", 10 * 60 * 60 * 1000L);
    }

    @Transactional
    public BuyerEntity registerBuyer(BuyerDTO buyerDto) {
    BuyerEntity buyer = new BuyerEntity();
    buyer.setBuyerId(buyerDto.getBuyerId());
    buyer.setPassword(passwordEncoder.encode(buyerDto.getPassword())); // Hashing added
    buyer.setNickname(buyerDto.getNickname());

    BuyerDetailEntity detail = new BuyerDetailEntity();
    detail.setPhone(buyerDto.getPhone());
    detail.setAddress(buyerDto.getAddress());
    detail.setAddressDetail(buyerDto.getAddressDetail());

    detail.setBuyer(buyer);
    buyer.setBuyerDetail(detail);

        return buyerRepository.save(buyer);
    }
}