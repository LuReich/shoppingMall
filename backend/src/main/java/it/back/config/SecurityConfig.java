package it.back.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.back.admin.repository.AdminRepository;
import it.back.buyer.repository.BuyerRepository;
import it.back.common.utils.JWTUtils;
import it.back.filter.JWTFilter;
import it.back.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtils, adminRepository, buyerRepository, sellerRepository);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스는 Spring Security 필터를 완전히 우회
        return (web) -> web.ignoring()
                .requestMatchers("/product/**", "/temp/**", "/images/**", "/buyerinquiry/**", "/sellerinquiry/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                // 공개 엔드포인트 (인증 불필요)
                .requestMatchers("/api/v1/buyer/register", "/api/v1/buyer/login").permitAll()
                .requestMatchers("/api/v1/buyer/check-buyerId", "/api/v1/buyer/check-email",
                        "/api/v1/buyer/check-phone").permitAll()
                .requestMatchers("/api/v1/seller/register", "/api/v1/seller/login").permitAll()
                .requestMatchers("/api/v1/seller/check-sellerId", "/api/v1/seller/check-email",
                        "/api/v1/seller/check-businessRegistrationNumber").permitAll()
                .requestMatchers("/api/v1/seller/public/**").permitAll()
                .requestMatchers("/api/v1/admin/login").permitAll()
                .requestMatchers("/api/v1/category/**").permitAll()
                .requestMatchers("/api/v1/faq/list", "/api/v1/faq/{faqId}").permitAll()
                // 상품 관련 - 조회는 공개, 수정은 SELLER/ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/product/list").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/product/{productId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/product/{productId}/detail").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/product/{productId}/review").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/product/create").hasRole("SELLER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/product/**").hasAnyRole("SELLER", "ADMIN")
                // 리뷰 관련 - BUYER만 작성/수정/삭제
                .requestMatchers("/api/v1/review/write").hasRole("BUYER")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/review/{reviewId}").hasRole("BUYER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/review/{reviewId}").hasRole("BUYER")
                // 구매자 관련 - BUYER 권한 필요
                .requestMatchers("/api/v1/cart/**").hasRole("BUYER")
                .requestMatchers("/api/v1/orders/**").hasRole("BUYER")
                .requestMatchers("/api/v1/buyer/**").hasRole("BUYER")
                // 판매자 관련 - SELLER 권한 필요
                .requestMatchers("/api/v1/seller/**").hasRole("SELLER")
                // 관리자 관련 - ADMIN만 접근
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", 
                              "http://localhost:4000", "http://3.105.143.5:4000",
                               "http://ourshop.monster:4000")); // Add
        // frontend
        // dev
        // server
        // port
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
