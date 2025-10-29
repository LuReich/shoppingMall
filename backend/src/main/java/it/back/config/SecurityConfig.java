package it.back.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/buyer/register", "/api/v1/buyer/login").permitAll()
                .requestMatchers("/api/v1/buyer/check-email", "/api/v1/buyer/check-phone").permitAll()
                .requestMatchers("/api/v1/seller/register", "/api/v1/seller/login").permitAll()
                .requestMatchers("/api/v1/admin/login").permitAll()
                .requestMatchers("/api/v1/seller/public/**").permitAll()
                .requestMatchers("/api/v1/review/write").hasRole("BUYER")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/review/{reviewId}").hasRole("BUYER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/review/{reviewId}").hasRole("BUYER")
                .requestMatchers("/api/v1/buyer/me").hasRole("BUYER")
                .requestMatchers("/api/v1/seller/me").hasRole("SELLER")
                .requestMatchers("/api/v1/admin/me").hasRole("ADMIN")
                .requestMatchers("/api/v1/cart/**").hasRole("BUYER")
                .requestMatchers("/api/v1/orders/**").hasRole("BUYER")
                .requestMatchers("/api/v1/category/**").permitAll()
                .requestMatchers("/api/v1/product/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/buyer/**").hasAnyRole("BUYER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/seller/**").hasAnyRole("SELLER", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:4000")); // Add
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
