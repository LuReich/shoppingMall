package it.back.security.service;

import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import it.back.security.entity.SecureUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecureUserService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find in Admin
        AdminEntity admin = adminRepository.findByAdminId(username).orElse(null);
        if (admin != null) {
            return new SecureUser(admin.getAdminId(), admin.getAdminPassword(), "ADMIN");
        }

        // Try to find in Buyer
        BuyerEntity buyer = buyerRepository.findByBuyerId(username).orElse(null);
        if (buyer != null) {
            return new SecureUser(buyer.getBuyerId(), buyer.getPassword(), "BUYER");
        }

        // Try to find in Seller
        SellerEntity seller = sellerRepository.findBySellerId(username).orElse(null);
        if (seller != null) {
            return new SecureUser(seller.getSellerId(), seller.getPassword(), "SELLER");
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}