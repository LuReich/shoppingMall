package it.back.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import it.back.admin.dto.UserSummaryDTO;
import it.back.admin.entity.AdminEntity;
import it.back.admin.repository.AdminRepository;
import it.back.buyer.entity.BuyerEntity;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.utils.JWTUtils;
import it.back.seller.entity.SellerEntity;
import it.back.seller.repository.SellerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("[JWTFilter] doFilterInternal 진입: " + request.getRequestURI());
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
            Long uid = jwtUtils.getUid(token); // uid 추출 메서드가 있다고 가정
            String nickname = jwtUtils.getUserNickname(token); // 닉네임 추출 메서드가 있다고 가정

            // details에 Map으로 추가 정보 저장
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("role", role);
            details.put("uid", uid);
            details.put("nickname", nickname);

            System.out.println("[JWTFilter] loginId: " + loginId);
            System.out.println("[JWTFilter] role: " + role);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, null, Collections.singletonList(() -> "ROLE_" + role));
            System.out.println("[JWTFilter] authorities: " + authToken.getAuthorities());
            authToken.setDetails(details);
            System.out.println("[JWTFilter] details: " + details);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // 토큰 파싱/검증 실패 시 인증 없이 다음 필터로 넘김
            System.err.println("[JWTFilter] 토큰 파싱/검증 실패: " + e.getMessage());
            e.printStackTrace();
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
