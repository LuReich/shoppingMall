package it.back.filter;

public class LoginFilter {
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L; // 1 hour
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L; // 24 hours
}
