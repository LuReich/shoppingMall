package it.back.security.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.back.admin.dto.UserSummaryDTO;

public class SecureUser implements UserDetails {

    private String loginId;
    private String password;
    private String role;

    public SecureUser(UserSummaryDTO userSummary) {
        this.loginId = userSummary.getLoginId();
        // Password is not typically stored in UserSummaryDTO for security context
        // It would be loaded by UserDetailsService if needed for authentication
        this.password = null; 
        this.role = userSummary.getRole();
    }

    public SecureUser(String loginId, String password, String role) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    // UserDetails 인터페이스는 getUsername()만 요구하므로, getUserId()는 별도 추가
    @Override
    public String getUsername() {
        return loginId;
    }

    public String getUserId() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}