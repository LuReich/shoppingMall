package it.back.common.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {

    // 쿠키 저장
    public Cookie createCookie(String name, Object value, int maxAge) {
        Cookie cookie = new Cookie(name, String.valueOf(value));
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 금지

        return cookie;
    }

    // 토큰을 등록
    public void addCookie(Cookie cookie, HttpServletResponse response) {

        response.addCookie(cookie);

    }

    // 쿠키 조회
    public Cookie getCookie(HttpServletRequest request, String name) {

        Cookie[] cookies=request.getCookies();

        if(cookies==null) {
            return null;
        }

        // 쿠키 찾으면 바로 return 하고 함수 종료
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals(name)) {
                return cookie;
            }
        }

        // 해당 이름 쿠키가 존재하지 않음
        return null;
    }

    // 쿠키 삭제
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {

        Cookie cookie=getCookie(request, name);

        if(cookie!=null) {

            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);    // 유효 시간을 0으로 두는 것이 삭제
            response.addCookie(cookie);

        }
    }
}
