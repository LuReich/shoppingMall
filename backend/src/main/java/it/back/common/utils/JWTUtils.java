package it.back.common.utils;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 발급 을 위한 유틸
 */
@Component
@Slf4j
public class JWTUtils {

    // uid 추출
    public Long getUid(String token) {
        Object value = Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("uid");
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // userNickname 추출
    public String getUserNickname(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("userNickname", String.class);
    }

    private SecretKey secretKey;

    public JWTUtils(@Value("${spring.jwt.secretKey}") String secret) {
        // JWT 토큰을 만들기 위한 비공개키를 h256 알고리즘을 통해 생성 
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // uid, userId, userNickname, role, 지속시간(분)
    public String createJwt(String category, Long uid, String userId, String userNickname, String userRole, Long mins) {
        return Jwts.builder()
                .claim("category", category)
                .claim("uid", uid)
                .claim("userId", userId)
                .claim("userNickname", userNickname)
                .claim("role", userRole)
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .expiration(Timestamp.valueOf(LocalDateTime.now().plusMinutes(mins)))
                .signWith(secretKey)
                .compact();
    }

    // 기존 방식과의 호환을 위해 오버로딩(기존 userName은 null)
    public String createJwt(Long uid, String userId, String userNickname, String userRole, Long expiredMs) {
        return createJwt("auth", uid, userId, userNickname, userRole, expiredMs / (60 * 1000));
    }

    // 기존 방식(사용 비권장)
    public String createJwt(String userId, String userRole, Long expiredMs) {
        return createJwt("auth", null, userId, null, userRole, expiredMs / (60 * 1000));
    }

    //JWT 토큰 유효성 체크
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된  JWT 토큰입니다");
        }
        return false;
    }

    //토큰 카데고리 분석
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("category", String.class);
    }

    //아이디 추출
    public String getUserId(String token) {

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("userId", String.class);
    }

    //이름 추출
    public String getUserName(String token) {

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("userName", String.class);
    }

    //권한 추출
    public String getUserRole(String token) {

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().get("role", String.class);
    }

    //유효시간 체크 
    public boolean getExpired(String token) {

        //현재 시간이 유효시간보다 이전인지 체크 
        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().getExpiration().before(Timestamp.valueOf(LocalDateTime.now()));
    }
}
