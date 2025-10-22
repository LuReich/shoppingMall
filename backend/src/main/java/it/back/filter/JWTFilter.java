package it.back.filter;

import it.back.admin.dto.UserSummaryDTO;
// Admin, Buyer import 제거 (Entity 네이밍만 사용)
import it.back.admin.entity.AdminEntity;
import it.back.buyer.entity.BuyerEntity;
import it.back.seller.entity.SellerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.seller.repository.SellerRepository;
import it.back.common.utils.JWTUtils;
import it.back.admin.repository.AdminRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);
        try {
            if (jwtUtils.getExpired(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            String loginId = jwtUtils.getUserId(token);
            String role = jwtUtils.getUserRole(token);
            UserSummaryDTO userSummary = createUserSummary(loginId, role);
            Authentication authToken = new UsernamePasswordAuthenticationToken(userSummary, null, Collections.singletonList(() -> "ROLE_" + role));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // 토큰 파싱/검증 실패 시 인증 없이 다음 필터로 넘김
        }
        filterChain.doFilter(request, response);
    }

    private UserSummaryDTO createUserSummary(String loginId, String role) {
        // This is a simplified creation. In a real app, you might fetch the user from DB
        // to ensure they still exist and are active. But for setting security context,
        // info from a trusted JWT is often sufficient.
        if ("ADMIN".equals(role)) {
            AdminEntity admin = adminRepository.findByAdminId(loginId).orElse(null);
            if (admin != null) {
                return new UserSummaryDTO(admin);
            }
        } else if ("BUYER".equals(role)) {
            BuyerEntity buyer = buyerRepository.findByBuyerId(loginId).orElse(null);
            if (buyer != null) {
                return new UserSummaryDTO(buyer);
            }
        } else if ("SELLER".equals(role)) {
            SellerEntity seller = sellerRepository.findBySellerId(loginId).orElse(null);
            if (seller != null) {
                return new UserSummaryDTO(seller);
            }
        }
        return null;
    }
}
