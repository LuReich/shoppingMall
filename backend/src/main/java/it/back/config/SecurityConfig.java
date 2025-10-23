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

<<<<<<< HEAD
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/product/**").permitAll()
                .requestMatchers("api/category/**").permitAll()
                .requestMatchers("/api/buyer/register", "/api/buyer/login").permitAll()
                .requestMatchers("/api/seller/register", "/api/seller/login").permitAll()
                .requestMatchers("/api/admin/login").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/buyer/**").hasAnyRole("BUYER", "ADMIN")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
        );
=======
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/buyer/register", "/api/buyer/login").permitAll()
        .requestMatchers("/api/seller/register", "/api/seller/login").permitAll()
        .requestMatchers("/api/admin/login").permitAll()
        .requestMatchers("/api/product/**").permitAll()
        .requestMatchers("/api/category/**").permitAll()
        .requestMatchers(HttpMethod.PATCH, "/api/buyer/**").hasAnyRole("BUYER", "ADMIN")
        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")
        .requestMatchers("/**").hasAnyRole("ADMIN")
        .anyRequest().authenticated()
    );
>>>>>>> 3826cc4f115e5dd281e18199a0a3ce70c01cc024

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:4000")); // Add frontend dev server port
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
