package it.back.common.dto;


import it.back.common.utils.TimeFormatUtils;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private String date;
    private int status;
    private T response;

    public ApiResponse(int status, T response) {
        this.status = status;
        this.response = response;
        this.date = TimeFormatUtils.getDateTime();
    }

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(200, response);
    }

   
}
