package it.back.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.back.common.utils.TimeFormatUtils;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"status", "date", "content"})
public class ApiResponse<T> {

    private String date;
    private int status;
    private T content;

    public ApiResponse(int status, T content) {
        this.status = status;
        this.date = TimeFormatUtils.getDateTime();
        this.content = content;
    }

    public static <T> ApiResponse<T> ok(T content) {
        return new ApiResponse<>(200, content);
    }

    // 에러 응답 생성 (상태코드와 메시지)
    public static <T> ApiResponse<T> error(int status, T message) {
        return new ApiResponse<>(status, message);
    }

    // 400 Bad Request
    public static ApiResponse<String> badRequest(String message) {
        return error(400, message);
    }

    // 403 Forbidden
    public static ApiResponse<String> forbidden(String message) {
        return error(403, message);
    }

    // 404 Not Found
    public static ApiResponse<String> notFound(String message) {
        return error(404, message);
    }

    

}
